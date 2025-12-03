package com.bob.core.application.member;

import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.member.dto.command.RegisterMemberWishCommand;
import com.bob.core.application.member.dto.command.RemoveMemberWishCommand;
import com.bob.core.application.member.port.in.MemberReader;
import com.bob.core.application.member.port.in.MemberWishManager;
import com.bob.core.application.member.port.out.MemberBookPort;
import com.bob.core.domain.member.Member;
import com.bob.core.domain.member.MemberWish;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberWishCommandService implements MemberWishManager {

    private final MemberReader memberReader;

    private final MemberBookPort bookPort;

    @Override
    public Member registerWish(UUID memberId, RegisterMemberWishCommand command) {
        Member member = memberReader.read(memberId);

        Long bookId = bookPort.register(
            command.isbn(), command.title(), command.author(),
            command.description(), command.priceStandard(), command.cover(), command.pubDate()
        );

        verifyWishDuplicate(member, bookId);

        member.addWish(bookId);

        return member;
    }

    private static void verifyWishDuplicate(Member member, Long bookId) {
        if (member.getWishes().stream().map(MemberWish::getBookId).anyMatch((exist) -> Objects.equals(exist, bookId)))
            throw new ApplicationException(ApplicationError.MEMBER_WISH_DUPLICATED);
    }

    @Override
    public Member removeWish(UUID memberId, RemoveMemberWishCommand command) {
        Member member = memberReader.read(memberId);

        member.removeWish(command.wishId());

        return member;
    }
}
