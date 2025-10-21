package com.bob.web.chat.adapter;

import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.bob.domain.chat.service.dto.command.CreateChatRoomCommand;
import com.bob.domain.chat.service.dto.response.CreateChatRoomResponse;
import com.bob.domain.chat.usecase.ChatRoomWriteUseCase;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("TradeChatAdapter 테스트")
class TradeChatAdapterTest {

  @InjectMocks
  private TradeChatAdapter adapter;

  @Mock
  private ChatRoomWriteUseCase writeUseCase;

  @Captor
  private ArgumentCaptor<CreateChatRoomCommand> commandCaptor;

  @Test
  void 거래_채팅방_생성_기능_호출() {
    // given
    Long postId = 1L;
    Long tradeId = 1L;
    UUID buyerId = OTHER_MEMBER_ID;
    boolean isFar = true;

    CreateChatRoomResponse response = mock(CreateChatRoomResponse.class);
    given(response.chatRoomId()).willReturn(1L);
    given(writeUseCase.createChatRoomProcess(any(CreateChatRoomCommand.class))).willReturn(response);

    // when
    Long chatRoomId = adapter.create(postId, tradeId, buyerId, isFar);

    // then
    assertThat(chatRoomId).isEqualTo(1L);
    then(writeUseCase).should(times(1)).createChatRoomProcess(commandCaptor.capture());

    CreateChatRoomCommand command = commandCaptor.getValue();
    assertThat(command.postId()).isEqualTo(postId);
    assertThat(command.tradeId()).isEqualTo(tradeId);
    assertThat(command.buyerId()).isEqualTo(buyerId);
    assertThat(command.isFar()).isTrue();
  }
}
