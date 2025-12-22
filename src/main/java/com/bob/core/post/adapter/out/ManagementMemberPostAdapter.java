package com.bob.core.post.adapter.out;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.admin.member.application.port.out.ManagementMemberPostPort;
import com.bob.admin.member.application.port.result.activity.ManagementMemberPost;
import com.bob.core.post.application.dto.query.ReadMemberPostsQuery;
import com.bob.core.post.application.port.in.PostReader;
import com.bob.core.post.domain.Post;

@Component
@RequiredArgsConstructor
public class ManagementMemberPostAdapter implements ManagementMemberPostPort {

    private final PostReader postReader;

    @Override
    public ManagementMemberPost read(UUID writerId) {
        ReadMemberPostsQuery query = new ReadMemberPostsQuery(writerId);

        List<Post> posts = postReader.readByMember(query);

        List<Long> postIds = posts.stream().map(Post::getId).toList();

        return new ManagementMemberPost(postIds.size(), postIds);
    }
}
