package com.bob.core.application.post;

import static com.bob.core.domain.post.status.Status.DEACTIVATED;
import static com.bob.core.domain.post.status.TradeProgress.valueOf;
import static com.bob.global.exception.response.ApplicationError.POST_OWNER_REQUIRED;
import static com.bob.global.exception.response.ApplicationError.POST_UNREMOVABLE_STATE;
import static com.bob.global.exception.response.ApplicationError.POST_VERIFIED_AREA_REQUIRED;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.post.dto.command.ChangeMemberPostStatusCommand;
import com.bob.core.application.post.dto.command.ChangePostInfoCommand;
import com.bob.core.application.post.dto.command.ChangePostTradeProgressCommand;
import com.bob.core.application.post.dto.command.CreatePostCommand;
import com.bob.core.application.post.dto.command.RemovePostCommand;
import com.bob.core.application.post.dto.query.ReadMemberPostsQuery;
import com.bob.core.application.post.port.in.PostCreator;
import com.bob.core.application.post.port.in.PostModifier;
import com.bob.core.application.post.port.in.PostReader;
import com.bob.core.application.post.port.out.PostAreaPort;
import com.bob.core.application.post.port.out.PostBookcasePort;
import com.bob.core.application.post.port.out.PostFilePort;
import com.bob.core.application.post.port.out.PostMemberPort;
import com.bob.core.application.post.port.result.PostArea;
import com.bob.core.application.post.port.result.PostBookcaseId;
import com.bob.core.application.post.port.result.PostMember;
import com.bob.core.domain.post.Post;
import com.bob.core.domain.post.repository.PostRepository;
import com.bob.global.exception.exceptions.ApplicationException;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommandService implements PostCreator, PostModifier {

    private final PostRepository postRepository;
    private final PostReader postReader;

    private final PostFilePort filePort;
    private final PostAreaPort areaPort;
    private final PostMemberPort memberPort;
    private final PostBookcasePort memberBookcasePort;

    @Override
    public Post create(CreatePostCommand command) {
        PostMember postMember = memberPort.read(command.memberId());
        verifyAreaAuthentication(postMember.authenticated());

        PostArea area = areaPort.read(postMember.emdId());

        PostBookcaseId bookcaseItem = memberBookcasePort.register(command.toRegisterItemRequest());

        Post post = savePost(command, bookcaseItem.bookId(), bookcaseItem.id(), area);

        memberBookcasePort.allocate(command.memberId(), post.getId(), bookcaseItem.id());

        fileMapping(command.fileNames(), post.getId());

        return post;
    }

    private static void verifyAreaAuthentication(boolean validity) {
        if (!validity)
            throw new ApplicationException(POST_VERIFIED_AREA_REQUIRED);
    }

    private Post savePost(CreatePostCommand command, Long bookId, Long sellerBookId, PostArea areaSummary) {
        return postRepository.save(
            Post.createPost(command.categoryId(), areaSummary.emdId(), bookId,
                command.bookTitle(), command.description(), command.bookCover(),
                command.bookStatus(), command.memberId(), sellerBookId, command.bookPriceStandard(), command.wishOnly()
            )
        );
    }

    private void fileMapping(List<String> fileNames, Long postId) {
        if (fileNames == null || fileNames.isEmpty())
            return;

        filePort.changeReferenceId(fileNames, String.valueOf(postId));
    }

    @Override
    public Post changePostInfo(Long postId, ChangePostInfoCommand command) {
        Post post = postReader.read(postId);
        verifyPostOwner(command.memberId(), post.getWriterId());

        post.updateInfo(command.bookStatus(), command.description(), command.wishOnly());

        return post;
    }

    @Override
    public Post changePostTradeProgress(Long postId, ChangePostTradeProgressCommand command) {
        Post post = postReader.read(postId);

        post.updateTradeProgress(valueOf(command.status()));

        return post;
    }

    @Override
    public Post activate(Long postId) {
        Post post = postReader.read(postId);

        post.activate();

        return post;
    }

    @Override
    public Post deactivate(Long postId, RemovePostCommand command) {
        Post post = postReader.read(postId);
        verifyPostOwner(command.memberId(), post.getWriterId());
        verifyDeletable(post);

        post.deactivate();

        memberBookcasePort.free(post.getId());

        return post;
    }

    private static void verifyPostOwner(UUID requestMemberId, UUID postMemberId) {
        if (!Objects.equals(requestMemberId, postMemberId))
            throw new ApplicationException(POST_OWNER_REQUIRED);
    }

    private static void verifyDeletable(Post post) {
        if (post.isReserved())
            throw new ApplicationException(POST_UNREMOVABLE_STATE);
    }

    @Override
    public void changeStatusByAccountEvent(ChangeMemberPostStatusCommand command) {
        postReader.readByMember(new ReadMemberPostsQuery(command.memberId())).forEach(p -> {
            if (command.status() == DEACTIVATED && p.isActive())
                p.deactivate();
            else
                p.activate();
        });
    }
}
