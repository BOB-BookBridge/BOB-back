package com.bob.global.exception.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApplicationError {

    // 공통 예외
    SERVER_ERROR("요청을 처리할 수 없습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_CHANGES("변경 사항이 없습니다.", HttpStatus.BAD_REQUEST),

    // 회원 예외
    MEMBER_BANNED("제한 조치된 계정입니다.", HttpStatus.FORBIDDEN),
    MEMBER_MAIL_CODE_EXPIRED("이메일 인증 코드가 만료되었습니다.", HttpStatus.GONE),
    MEMBER_MAIL_CODE_MISMATCH("이메일 인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_EMAIL_UNVERIFIED("인증되지 않은 이메일입니다.", HttpStatus.UNAUTHORIZED),
    MEMBER_EMAIL_DUPLICATED("중복된 이메일입니다.", HttpStatus.CONFLICT),
    MEMBER_PASSWORD_MISMATCH("이전 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_WISH_DUPLICATED("희망 도서가 이미 등록되어 있습니다.", HttpStatus.CONFLICT),

    // 책장 예외
    BOOKCASE_ITEM_ACCESS_DENIED("다른 사용자의 도서는 사용할 수 없습니다.", HttpStatus.FORBIDDEN),
    BOOKCASE_ITEM_UNAVAILABLE("사용할 수 없는 책이 포함되어 있습니다. 책: [%s]", HttpStatus.BAD_REQUEST),
    BOOKCASE_ITEM_ALREADY_USE("교환 물품으로 사용되는 책이 포함되어 있습니다. 교환 게시글: [#%d], 책: [%s]", HttpStatus.CONFLICT),
    BOOKCASE_ITEM_UNREMOVABLE("교환 물품으로 사용되는 책은 삭제할 수 없습니다. 교환 게시글: [#%d]", HttpStatus.CONFLICT),

    // 지역 예외
    AREA_AUTHENTICATION_FAILED("현재 위치를 인증할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 게시글 예외
    POST_ACCESS_DENIED("게시글에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),
    POST_OWNER_REQUIRED("게시글 작성자만 수정할 수 있습니다.", HttpStatus.FORBIDDEN),
    POST_VERIFIED_AREA_REQUIRED("위치 인증을 한 회원만이 게시글을 작성할 수 있습니다.", HttpStatus.FORBIDDEN),
    POST_UNREMOVABLE_STATE("예약중인 게시글은 삭제할 수 없습니다.", HttpStatus.CONFLICT),

    // 게시글 찜 예외
    POST_FAVORITE_EXIST("이미 찜한 게시글입니다.", HttpStatus.CONFLICT),
    POST_FAVORITE_NOT_EXIST("찜하지 않은 게시글입니다.", HttpStatus.CONFLICT),

    // 채팅 예외
    CHATROOM_ACCESS_DENIED("참여중이지 않은 채팅방에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),

    // 파일 예외
    FILE_ACCESS_DENIED("파일을 수정할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 거래 예외
    TRADE_ACCESS_DENIED("거래에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    TRADE_SELF_NOT_ALLOWED("자신과의 거래는 불가능합니다.", HttpStatus.BAD_REQUEST),
    TRADE_POST_REMOVED("삭제된 게시글은 거래 요청이 불가능합니다.", HttpStatus.BAD_REQUEST),
    TRADE_ALREADY_PROCESSED("다른 회원과 거래가 진행중이거나 완료된 상태입니다.", HttpStatus.CONFLICT),
    TRADE_STATUS_UNCHANGED("변경하려는 거래 상태와 현재 상태가 동일합니다.", HttpStatus.BAD_REQUEST),
    TRADE_STATUS_NOT_CHANGEABLE("거래 상태를 변경할 수 없습니다.", HttpStatus.CONFLICT),
    TRADE_STATUS_ALREADY_ABORTED("거래가 이미 중단되었습니다.", HttpStatus.CONFLICT),
    TRADE_STATUS_ALREADY_COMPLETED("거래가 이미 완료되었습니다.", HttpStatus.CONFLICT),
    TRADE_REMOVE_ONLY_REQUESTER("거래 삭제는 거래 요청자만 가능합니다.", HttpStatus.FORBIDDEN),
    TRADE_REMOVE_ONLY_ABORTED("거래 삭제는 취소, 거절 단계에서만 가능합니다.", HttpStatus.CONFLICT),
    TRADE_MAIN_ITEM_UNCHANGEABLE("거래 대표 물품은 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
    TRADE_ITEM_UNCHANGEABLE("예약, 완료 상태의 거래는 거래 물품 변경이 불가능합니다.", HttpStatus.CONFLICT),
    TRADE_SELLER_WISH_NOT_MATCH("요청한 거래 물품이 판매자의 희망 도서에 해당하지 않습니다.", HttpStatus.BAD_REQUEST),

    // 알림 예외
    NOTIFICATION_ACCESS_DENIED("알림에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 문의 예외
    INQUIRY_ACCESS_DENIED("문의에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 금칙어 예외
    FILTER_WORD_ALREADY_EXISTS("이미 등록된 금칙어입니다.", HttpStatus.CONFLICT),
    FILTER_WORD_NOT_EDITABLE("사전 정의된 금칙어는 수정/삭제할 수 없습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
