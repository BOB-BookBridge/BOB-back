package com.bob.domain.member.service;

import static com.bob.support.fixture.domain.InterestFixture.customInterest;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.defaultIdMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyCollection;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

import com.bob.domain.member.entity.Interest;
import com.bob.domain.member.entity.Member;
import com.bob.domain.member.entity.MemberInterest;
import com.bob.domain.member.repository.InterestRepository;
import com.bob.domain.member.repository.MemberInterestRepository;
import com.bob.domain.member.repository.projection.InterestEntry;
import com.bob.domain.member.service.reader.MemberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("회원 관심사 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MemberInterestServiceTest {

  @InjectMocks
  MemberInterestService service;

  @Mock
  MemberInterestRepository memberInterestRepository;

  @Mock
  InterestRepository interestRepository;

  @Mock
  MemberReader memberReader;

  @Test
  void 관심사_변경_신규_추가() {
    // given
    Member member = defaultIdMember();
    given(memberReader.readMemberById(member.getId())).willReturn(member);
    given(memberInterestRepository.findCanonicalNameWithId(member.getId())).willReturn(List.of());

    List<String> raw = List.of("JAVA", "Go");
    List<Interest> foundAfterUpsert = List.of(customInterest(1L, "java"), customInterest(2L, "go"));
    given(interestRepository.findByCanonicalNameIn(new LinkedHashSet<>(List.of("java", "go")))).willReturn(foundAfterUpsert);

    ArgumentCaptor<Collection<MemberInterest>> saveCaptor = ArgumentCaptor.forClass(Collection.class);

    // when
    service.changeMemberInterests(member.getId(), raw);

    // then
    then(interestRepository).should().upsert("java");
    then(interestRepository).should().upsert("go");
    then(interestRepository).should().findByCanonicalNameIn(new LinkedHashSet<>(List.of("java", "go")));
    then(memberInterestRepository).should().saveAll(saveCaptor.capture());

    List<MemberInterest> saved = new ArrayList<>(saveCaptor.getValue());
    assertThat(saved).hasSize(2);
    assertThat(saved.stream().map(MemberInterest::getDisplayName)).containsExactlyInAnyOrder("JAVA", "Go");
    assertThat(saved.stream().map(MemberInterest::getInterestId)).containsExactlyInAnyOrder(1L, 2L);
  }

  @Test
  void 관심사_변경_일부_추가_일부_삭제() {
    // given
    Member member = defaultIdMember();
    given(memberReader.readMemberById(member.getId())).willReturn(member);

    InterestEntry entry1 = new InterestEntry(1L, "java");
    InterestEntry entry2 = new InterestEntry(2L, "spring");
    given(memberInterestRepository.findCanonicalNameWithId(MEMBER_ID)).willReturn(List.of(entry1, entry2));

    List<String> raw = List.of("Java", "Kotlin");
    given(interestRepository.findByCanonicalNameIn(anyCollection())).willReturn(List.of(customInterest(3L, "kotlin")));

    ArgumentCaptor<Collection> saveCaptor = ArgumentCaptor.forClass(Collection.class);
    ArgumentCaptor<Collection> deleteCaptor = ArgumentCaptor.forClass(Collection.class);

    // when
    service.changeMemberInterests(MEMBER_ID, raw);

    // then
    then(interestRepository).should().upsert("kotlin");
    then(interestRepository).should(never()).upsert("java");
    then(memberInterestRepository).should().saveAll(saveCaptor.capture());

    List<MemberInterest> saved = new ArrayList<>(saveCaptor.getValue());
    assertThat(saved).hasSize(1);
    assertThat(saved.get(0).getInterestId()).isEqualTo(3L);
    assertThat(saved.get(0).getDisplayName()).isEqualTo("Kotlin");

    then(memberInterestRepository).should().deleteByMemberIdAndInterestIds(eq(MEMBER_ID), deleteCaptor.capture());
    assertThat(deleteCaptor.getValue()).containsExactly(2L); // spring
  }

  @Test
  void 관심사_변경_대소문자_중복_시_단일_처리() {
    // given
    Member member = defaultIdMember();
    given(memberReader.readMemberById(member.getId())).willReturn(member);
    given(memberInterestRepository.findCanonicalNameWithId(MEMBER_ID)).willReturn(List.of());

    List<String> raw = List.of("Java", "java");
    given(interestRepository.findByCanonicalNameIn(new LinkedHashSet<>(List.of("java"))))
        .willReturn(List.of(customInterest(1L, "java")));
    ArgumentCaptor<Collection> saveCaptor = ArgumentCaptor.forClass(Collection.class);

    // when
    service.changeMemberInterests(MEMBER_ID, raw);

    // then
    then(interestRepository).should().upsert("java");
    then(memberInterestRepository).should().saveAll(saveCaptor.capture());

    List<MemberInterest> saved = new ArrayList<>(saveCaptor.getValue());
    assertThat(saved).hasSize(1);
    assertThat(saved.get(0).getDisplayName()).isEqualTo("Java");
    assertThat(saved.get(0).getInterestId()).isEqualTo(1L);
  }

  @Test
  void 관심사_변경_공백_Null_정리() {
    // given
    Member member = defaultIdMember();
    given(memberReader.readMemberById(member.getId())).willReturn(member);
    given(memberInterestRepository.findCanonicalNameWithId(MEMBER_ID)).willReturn(List.of());

    List<String> raw = Arrays.asList(null, "  ", "  Java  ", "", "Go");
    given(interestRepository.findByCanonicalNameIn(new LinkedHashSet<>(List.of("java", "go"))))
        .willReturn(List.of(customInterest(1L, "java"), customInterest(2L, "go")));
    ArgumentCaptor<Collection> saveCaptor = ArgumentCaptor.forClass(Collection.class);

    // when
    service.changeMemberInterests(MEMBER_ID, raw);

    // then
    then(interestRepository).should().upsert("java");
    then(interestRepository).should().upsert("go");
    then(memberInterestRepository).should().saveAll(saveCaptor.capture());

    List<MemberInterest> saved = new ArrayList<>(saveCaptor.getValue());
    assertThat(saved).hasSize(2);
    assertThat(saved.stream().map(MemberInterest::getDisplayName)).containsExactlyInAnyOrder("Java", "Go");
    assertThat(saved.stream().map(MemberInterest::getInterestId)).containsExactlyInAnyOrder(1L, 2L);
  }

  @Test
  void 관심사_변경_없음() {
    // given
    Member member = defaultIdMember();
    given(memberReader.readMemberById(member.getId())).willReturn(member);

    List<String> raw = List.of("JAVA");
    InterestEntry entry = new InterestEntry(1L, "java");
    given(memberInterestRepository.findCanonicalNameWithId(MEMBER_ID)).willReturn(List.of(entry));

    // when
    service.changeMemberInterests(MEMBER_ID, raw);

    // then
    then(interestRepository).should(never()).upsert(anyString());
    then(memberInterestRepository).should(never()).saveAll(anyCollection());
    then(memberInterestRepository).should(never()).deleteByMemberIdAndInterestIds(any(), anyCollection());
  }

  @Test
  void 관심사_조회() {
    // given
    List<String> displays = List.of("Java", "Spring");
    given(memberInterestRepository.findDisplayNamesByMemberId(MEMBER_ID)).willReturn(displays);

    // when
    List<String> result = service.readMemberInterests(MEMBER_ID);

    // then
    assertThat(result).containsExactlyElementsOf(displays);
    then(memberInterestRepository).should().findDisplayNamesByMemberId(MEMBER_ID);
  }
}
