# 북스캠프 프론트엔드

## 📁 구조

```
templates/
├── fragments/          # 공통 컴포넌트 (헤더, 푸터, 사이드바)
├── layouts/           
│   ├── default.html    # 사이드바 있는 레이아웃
│   └── no-sidebar.html # 사이드바 없는 레이아웃
├── books/             # 도서 페이지들
├── index.html         # 메인
├── cart.html          # 장바구니
├── login.html         # 로그인
└── mypage.html        # 마이페이지
```

## ✍️ 새 페이지 만들기

### 1. 템플릿 작성 (사이드바 포함)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/default}">
<head>
    <title>페이지 제목</title>
</head>
<body>
    <div layout:fragment="content">
        <!-- 여기에 콘텐츠 작성 -->
        <h1>제목</h1>
        <p>내용...</p>
    </div>
</body>
</html>
```

### 2. 사이드바 없는 페이지

`layout:decorate="~{layouts/no-sidebar}"` 사용



## 💡 참고

- 헤더, 푸터, 사이드바는 레이아웃에 자동 포함됨
- `layout:fragment="content"` 영역만 작성하면 됨