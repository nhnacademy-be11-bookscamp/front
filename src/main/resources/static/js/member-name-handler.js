'use strict';

document.addEventListener("DOMContentLoaded", function() {


    function getCookie(name) {
        let value = "; " + document.cookie;
        let parts = value.split("; " + name + "=");
        if (parts.length === 2) return parts.pop().split(";").shift();
    }

    const encodedName = getCookie("member_name");

    if (encodedName) {
        const memberName = decodeURIComponent(encodedName.replace(/\+/g, ' '));

        sessionStorage.setItem("memberName", memberName);

        document.cookie = "member_name=; Path=/; Max-Age=0;";
    }

    const storedName = sessionStorage.getItem("memberName");

    if (storedName) {

        //    - 데스크톱 헤더 (header-member-name ID)
        const headerNameSpan = document.getElementById("header-member-name");
        if (headerNameSpan) {
            headerNameSpan.innerText = storedName;
        }

        //    - 모바일 헤더 (mobile-member-name ID)
        const mobileNameSpan = document.getElementById("mobile-member-name");
        if (mobileNameSpan) {
            mobileNameSpan.innerText = storedName;
        }
    }
});

