# 📚 북스캠프 프론트엔드

Spring Boot + Thymeleaf 기반 온라인 서점 프론트엔드

## 📁 프로젝트 구조

### 템플릿 구조
```
src/main/resources/templates/
├── layout/
│   ├── default.html        # 기본 레이아웃 (헤더+푸터+사이드바)
│   └── no-sidebar.html     # 사이드바 없는 레이아웃
│
├── fragment/
│   ├── header.html         # 공통 헤더
│   ├── footer.html         # 공통 푸터
│   ├── category.html       # 카테고리 사이드바
│   ├── category-select.html # 카테고리 선택 컴포넌트
│   └── admin-sidebar.html  # 관리자 사이드바
│
├── admin/                  # 관리자 페이지
│   ├── index.html         # 관리자 메인
│   ├── dashboard.html     # 대시보드
│   ├── books.html         # 도서 목록 관리
│   └── category.html      # 카테고리 관리
│
├── book/                   # 도서 페이지
│   ├── create.html        # 도서 등록
│   ├── update.html        # 도서 수정
│   └── detail.html        # 도서 상세
│
├── aladin/                 # 알라딘 API
│   ├── search.html        # 도서 검색
│   └── create.html        # API 도서 등록
│
├── member/                 # 회원 페이지
│   ├── login.html         # 로그인
│   ├── signup.html        # 회원가입
│   └── address/           # 주소 관리
│       ├── list.html      # 주소 목록
│       ├── new.html       # 주소 등록
│       └── edit.html      # 주소 수정
│
├── tags/
│   └── tag.html           # 태그 페이지
│
├── error/
│   └── error.html         # 에러 페이지
│
└── index.html              # 메인 페이지
```

## 📝 뷰 개발 가이드

### 1. 기본 레이아웃 사용 (사이드바 포함)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/default}">
<head>
    <title>페이지 제목 | 북스캠프</title>
</head>

<body>
<!-- Hero 섹션 숨김 (필요한 경우) -->
<section layout:fragment="hero" class="hero" style="display: none;">
</section>

<!-- 메인 콘텐츠 -->
<div layout:fragment="content">
    <section class="product spad">
        <div class="container">
            <div class="row">
                <!-- 여기에 콘텐츠 작성 -->
                <div class="col-lg-12">
                    <h1>페이지 내용</h1>
                </div>
            </div>
        </div>
    </section>
</div>

<!-- 추가 JavaScript (선택사항) -->
<div layout:fragment="extra-js">
    <script>
        // 커스텀 스크립트
    </script>
</div>
</body>
</html>
```

### 2. 사이드바 없는 레이아웃

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/no-sidebar}">
<head>
    <title>페이지 제목</title>
</head>

<body>
<div layout:fragment="content">
    <!-- 콘텐츠 작성 -->
</div>
</body>
</html>
```

### 3. 관리자 페이지 (사이드바 포함)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/default}">
<head>
    <title>관리자 페이지</title>
</head>

<body>
<section layout:fragment="hero" class="hero" style="display: none;"></section>

<div layout:fragment="content">
    <section class="product spad">
        <div class="container">
            <div class="row">
                <!-- 관리자 사이드바 -->
                <div class="col-lg-3">
                    <div class="hero__categories">
                        <div class="hero__categories__all">
                            <i class="fa fa-bars"></i>
                            <span>관리자 메뉴</span>
                        </div>
                        <ul th:replace="~{fragment/admin-sidebar :: sidebar}">
                            <li><a href="#">메뉴</a></li>
                        </ul>
                    </div>
                </div>

                <!-- 메인 콘텐츠 -->
                <div class="col-lg-9">
                    <div style="background: #ffffff; padding: 30px; border: 1px solid #ebebeb; border-radius: 4px;">
                        <h4 style="margin-bottom: 30px; font-weight: 700;">
                            <i class="fa fa-icon-name" style="margin-right: 10px; color: #7fad39;"></i>
                            페이지 제목
                        </h4>
                        <!-- 콘텐츠 -->
                    </div>
                </div>
            </div>
        </div>
    </section>
</div>
</body>
</html>
```