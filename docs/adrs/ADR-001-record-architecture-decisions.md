# ADR-001: Ghi lại các quyết định về kiến trúc



Date: 2026-03-01



## Status



Accepted



## Context



Trong suốt quá trình phát triển hệ thống, chúng ta phải đưa ra hàng loạt quyết định kỹ thuật có tầm ảnh hưởng nhỏ và

lớn. Nếu không

được ghi chép lại, những lý do đằng sau các quyết định này sẽ dần bị lãng quên theo thời gian, gây khó khăn cho việc bảo

trì và mở rộng. Vì vậy Chúng ta cần một phương pháp chuẩn hóa để lưu trữ ngữ cảnh và hệ quả của những lựa chọn kỹ thuật

này.



## Decision



Chúng tôi quyết định áp dụng template Architecture Decision Records theo Michael Nygard đề xuất tại bài

báo: http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions



## Reason



1. Lưu giữ bối cảnh

2. Minh bạch hóa thiết kế



## Consequences



- Tích cực: Tài liệu hóa các quyết định về kiến trúc, phản ánh quá trình tiến hóa của hệ thống.

- Đánh đổi: Đội ngũ phát triển sẽ phải tốn thêm thời gian quản lý để thảo luận và soạn thảo tài liệu cho mỗi quyết định.