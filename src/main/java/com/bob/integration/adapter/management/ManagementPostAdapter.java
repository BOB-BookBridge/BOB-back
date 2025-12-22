package com.bob.integration.adapter.management;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.management.application.port.out.ManagementPostPort;
import com.bob.core.management.application.port.result.activity.ManagementMemberPost;
import com.bob.core.post.application.dto.query.ReadMemberPostsQuery;
import com.bob.core.post.application.port.in.PostReader;
import com.bob.core.post.domain.Post;

@Component
@RequiredArgsConstructor
public class ManagementPostAdapter implements ManagementPostPort {

    private final PostReader postReader;

    @Override
    public ManagementMemberPost read(UUID writerId) {
        ReadMemberPostsQuery query = new ReadMemberPostsQuery(writerId);

        List<Post> posts = postReader.readByMember(query);

        List<Long> postIds = posts.stream().map(Post::getId).toList();

        return new ManagementMemberPost(postIds.size(), postIds);
    }
}
