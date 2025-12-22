package com.bob.core.member.application;

import static com.bob.global.exception.response.ApplicationError.MEMBER_WISH_DUPLICATED;

import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.member.application.dto.command.RegisterMemberWishCommand;
import com.bob.core.member.application.dto.command.RemoveMemberWishCommand;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.member.application.port.in.MemberWishManager;
import com.bob.core.member.application.port.out.MemberBookPort;
import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.MemberWish;
import com.bob.global.exception.exceptions.ApplicationException;

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
            throw new ApplicationException(MEMBER_WISH_DUPLICATED);
    }

    @Override
    public Member removeWish(UUID memberId, RemoveMemberWishCommand command) {
        Member member = memberReader.read(memberId);

        member.removeWish(command.wishId());

        return member;
    }
}
