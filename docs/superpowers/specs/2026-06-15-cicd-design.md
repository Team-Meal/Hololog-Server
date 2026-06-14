# CI/CD 파이프라인 설계

**날짜:** 2026-06-15  
**프로젝트:** Hololog-Server (Spring Boot 4.0.6 + Kotlin + Java 21)

---

## 목표

- PR 생성/업데이트 시 자동 테스트로 코드 품질 보장
- main 브랜치 merge 시 Docker 이미지 빌드 → GHCR push → 서버 자동 배포까지 자동화
- 방화벽 환경에서 self-hosted runner를 활용해 배포 서버 인바운드 포트 오픈 없이 배포

---

## 전체 흐름

```
PR 생성/업데이트
  └─ [test] Gradle 테스트 실행 (GitHub hosted runner)

main 브랜치 push (PR merge)
  └─ [test]        Gradle 테스트 실행 (GitHub hosted runner)
  └─ [build-push]  Docker 멀티스테이지 빌드 + GHCR push (GitHub hosted runner)
  └─ [deploy]      docker-compose pull & up --build (self-hosted runner on 배포 서버)
```

---

## 아키텍처

### Runner 구성

| Job | Runner 타입 | 이유 |
|-----|------------|------|
| `test` | `ubuntu-latest` (GitHub hosted) | 테스트는 외부 자원 불필요 |
| `build-push` | `ubuntu-latest` (GitHub hosted) | GHCR push는 외부 인터넷만 있으면 됨 |
| `deploy` | `self-hosted` (배포 서버 설치) | 방화벽으로 인해 외부에서 SSH 불가 — 서버에서 직접 실행 |

### Self-hosted Runner 동작 원리

배포 서버가 GitHub에 주기적으로 polling하여 작업을 가져오는 방식.  
인바운드 포트 오픈 불필요, 아웃바운드(HTTPS 443)만 열려 있으면 동작.

---

## 생성 파일 목록

```
.github/
└── workflows/
    └── cicd.yml          # 단일 워크플로 파일 (CI + CD 통합)

Dockerfile                # 멀티스테이지 빌드 (build 단계 + runtime 단계)
docker-compose.yml        # 배포 서버에서 컨테이너 관리
```

---

## 워크플로 상세 설계 (`cicd.yml`)

### 트리거

```yaml
on:
  pull_request:
    branches: [main]       # PR → test job만 실행
  push:
    branches: [main]       # main push → test + build-push + deploy 순서 실행
```

### Job: test

- **조건:** PR, main push 모두 실행
- **Runner:** `ubuntu-latest`
- **단계:**
  1. 코드 체크아웃
  2. Java 21 설정
  3. Gradle 캐시 설정 (`~/.gradle/caches`)
  4. `./gradlew test`

### Job: build-push

- **조건:** `push` to `main`만 실행 (`if: github.event_name == 'push'`)
- **의존성:** `test` job 성공 후 실행 (`needs: test`)
- **Runner:** `ubuntu-latest`
- **단계:**
  1. 코드 체크아웃
  2. GHCR 로그인 (`GHCR_TOKEN` secret 사용)
  3. Docker Buildx 설정
  4. Docker 빌드 + GHCR push
     - 이미지 태그: `ghcr.io/<owner>/hololog-server:latest` + `:<sha>`

### Job: deploy

- **조건:** `push` to `main`만 실행
- **의존성:** `build-push` job 성공 후 실행 (`needs: build-push`)
- **Runner:** `self-hosted`
- **단계:**
  1. GHCR 로그인
  2. `docker-compose pull`
  3. `docker-compose up -d`
  4. 미사용 이미지 정리 (`docker image prune -f`)

---

## Dockerfile 설계 (멀티스테이지)

### 1단계: build

- 베이스 이미지: `eclipse-temurin:21-jdk`
- Gradle Wrapper로 JAR 빌드 (`./gradlew bootJar`)
- 테스트는 CI에서 이미 실행했으므로 `-x test`로 스킵

### 2단계: runtime

- 베이스 이미지: `eclipse-temurin:21-jre` (JDK 불필요, 경량화)
- 1단계에서 빌드된 JAR만 복사
- 포트: `8080` expose
- 실행: `java -jar app.jar`

---

## docker-compose.yml 설계

- 서비스: `app` (Hololog-Server 컨테이너)
- 이미지: `ghcr.io/<owner>/hololog-server:latest`
- 포트: `8080:8080`
- 환경변수: `.env` 파일 참조 (`env_file: .env`)
- restart 정책: `unless-stopped`

> `.env` 파일은 배포 서버에 직접 관리 (DB URL, Redis URL, JWT 시크릿 등)  
> 절대 git에 커밋하지 않음 — `.gitignore`에 추가

---

## GitHub Secrets 설정

| Secret 이름 | 용도 | 발급 방법 |
|-------------|------|-----------|
| `GHCR_TOKEN` | GHCR 로그인 (read/write packages) | GitHub → Settings → Developer settings → PAT (classic) → `write:packages` 권한 |

---

## Self-hosted Runner 설치 방법 (배포 서버)

1. GitHub 저장소 → Settings → Actions → Runners → New self-hosted runner
2. 서버 OS에 맞는 설치 스크립트 실행
3. 서비스로 등록 (`./svc.sh install && ./svc.sh start`)
4. 러너 레이블: `self-hosted` (기본값 사용)

---

## 보안 고려사항

- `GHCR_TOKEN`은 GitHub Secrets에만 저장, 코드에 절대 하드코딩 금지
- DB 비밀번호, JWT 시크릿 등 민감 정보는 배포 서버의 `.env` 파일로 관리
- `.env`는 `.gitignore`에 추가
- self-hosted runner는 신뢰할 수 있는 서버에만 설치 (public fork PR 실행 주의)
