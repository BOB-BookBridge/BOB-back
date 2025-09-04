package com.bob.domain.member.service;

import com.bob.domain.member.entity.Interest;
import com.bob.domain.member.entity.Member;
import com.bob.domain.member.entity.MemberInterest;
import com.bob.domain.member.repository.InterestRepository;
import com.bob.domain.member.repository.MemberInterestRepository;
import com.bob.domain.member.repository.projection.InterestEntry;
import com.bob.domain.member.service.reader.MemberReader;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberInterestService {

  private final MemberReader memberReader;
  private final InterestRepository interestRepository;
  private final MemberInterestRepository memberInterestRepository;

  @Transactional
  public void changeMemberInterests(UUID memberId, List<String> rawNames) {
    Member member = memberReader.readMemberById(memberId);
    final Set<String> displayNames = normalizeNames(rawNames);
    //final Set<String> displayNames = new HashSet<>(rawNames);
    final Set<String> canonicalNames = convertCanonicalNames(displayNames);

    Map<String, Long> currentInterestsMap = memberInterestRepository.findCanonicalNameWithId(memberId).stream()
        .collect(Collectors.toMap(InterestEntry::canonicalName, InterestEntry::interestId));

    Set<String> newCanonicals = new LinkedHashSet<>(canonicalNames);
    newCanonicals.removeAll(currentInterestsMap.keySet());
    addInterests(member, newCanonicals, displayNames);

    Set<String> removeCanonicals = new LinkedHashSet<>(currentInterestsMap.keySet());
    removeCanonicals.removeAll(canonicalNames);
    removeMemberInterests(member, removeCanonicals, currentInterestsMap);
  }

  private static Set<String> normalizeNames(List<String> names) {
    if (names == null) {
      return Set.of();
    }
    return names.stream()
        .filter(Objects::nonNull)
        .map(String::trim)
        .filter(v -> !v.isEmpty())
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private static Set<String> convertCanonicalNames(Set<String> names) {
    return names.stream()
        .map(Interest::canonicalize)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private void addInterests(Member member, Set<String> newCanonicalNames, Set<String> displayNames) {
    if (newCanonicalNames.isEmpty()) {
      return;
    }
    Map<String, String> canonicalToDisplay = displayNames.stream()
        .collect(Collectors.toMap(Interest::canonicalize, s -> s, (a, b) -> a, LinkedHashMap::new));

    newCanonicalNames.forEach(interestRepository::upsert);

    Map<String, Interest> interestDict = interestRepository.findByCanonicalNameIn(newCanonicalNames).stream()
        .collect(Collectors.toMap(Interest::getCanonicalName, Function.identity()));

    memberInterestRepository.saveAll(newCanonicalNames.stream()
        .map(c -> MemberInterest.of(member.getId(), interestDict.get(c).getId(), canonicalToDisplay.get(c))).toList());
  }

  private void removeMemberInterests(Member member, Set<String> removeCanonicals, Map<String, Long> currentInterestsMap) {
    final List<Long> removeInterestIds = removeCanonicals.stream()
        .map(currentInterestsMap::get)
        .filter(Objects::nonNull)
        .toList();

    if (!removeInterestIds.isEmpty()) {
      memberInterestRepository.deleteByMemberIdAndInterestIds(member.getId(), removeInterestIds);
    }
  }

  @Transactional(readOnly = true)
  public List<String> readMemberInterests(UUID memberId) {
    return memberInterestRepository.findDisplayNamesByMemberId(memberId);
  }
}
