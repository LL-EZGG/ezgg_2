/** -----------------------------------------------------------------------------
 * SectionSelector.jsx
 * -----------------------------------------------------------------------------
 * 매칭 조건 설정 폼에서 사용되는 다목적 셀렉터 컴포넌트.
 * - 라인(TOP/JUG/MID/AD/SUP) 선택, 챔피언 선호/비선호 선택, 사용자 메모(30자) 입력 세 가지 모드를 가짐.
 * - props 로 전달받은 matchingCriteria 를 직접 수정하지 않고, 반드시 setMatchingCriteria
 *   콜백을 통해 상위 상태를 업데이트한다.
 * ---------------------------------------------------------------------------*/
import styled from '@emotion/styled';
import React, {useEffect, useRef, useState} from "react";
import {champions} from "../../data/champions.js";

/* ------------- 상수 ---------------------------------------------------------------- */

// 라인 버튼에 표시될 라벨 목록
const lines = ['TOP', 'JUG', 'MID', 'AD', 'SUP'];
// 사용자 메모 최대 글자 수
const TEXT_LIMIT = 30;

/* ------------- 보조 함수: 라인 선택 -------------------------------------------------------- */

/**
 * 라인 버튼 클릭 시 상태를 토글하는 함수
 * 같은 라인을 my/partner 양쪽에서 중복 선택하지 않도록 처리한다.
 * @param {('myLine'|'partnerLine')} type            - state 키(my 라인인지 partner 라인인지)
 * @param {string} line                              - 클릭된 라인 값
 * @param {object} matchingCriteria                  - 현재 매칭 기준 상태 객체
 * @param {Function} setMatchingCriteria             - 상태 업데이트 콜백
 */
const handleLineSelect = (type, line, matchingCriteria, setMatchingCriteria) => {
  const currentWantLine = matchingCriteria?.wantLine || {};
  const currentMyLine = currentWantLine.myLine || '';
  const currentPartnerLine = currentWantLine.partnerLine || '';

  // 중복 선택 상황 방지
  if (type === 'partnerLine' && line === currentMyLine) return;
  if (type === 'myLine' && line === currentPartnerLine) return;

  // 토글 로직
  const newWantLine = {
    ...currentWantLine,
    [type]: currentWantLine[type] === line ? '' : line
  };

  setMatchingCriteria({
    ...matchingCriteria,
    wantLine: newWantLine
  });
};

/* ------------- 메인 컴포넌트 ---------------------------------------------------------- */

/**
 * SectionSelector 컴포넌트
 * ---------------------------------------------------------------------------
 * 라인 선택 UI 혹은 챔피언 선택 UI를 렌더링하는 함수형 컴포넌트.
 *
 * @prop {object}   matchingCriteria          - 상위 컴포넌트의 상태 객체
 * @prop {Function} setMatchingCriteria       - 상태 변경 함수(상태를 불변성 지켜 갱신)

 * @prop {'line'|'champion'|'userPreferenceText'} type - 라인 선택 모드 or 챔피언 선택 모드 or 텍스트 입력 모드
 * @prop {'my'|'partner'|'preferred'|'banned'} kind - 세부 유형(라인: my/partner, 챔피언: preferred/banned)

 * @prop {string}   selectedValue             - (라인 모드) 현재 선택된 값
 * @prop {string}   disabledValue             - (라인 모드) 클릭 불가능한 값(상대가 이미 선택한 라인)
 */
const SectionSelector = ({
                           matchingCriteria,
                           setMatchingCriteria,
                           type,
                           kind,
                           selectedValue,
                           disabledValue,
                           children // 향후 확장용 (현재 미사용)
                         }) => {

  /* ----- 상태: 챔피언 검색어 및 UI 토글 ----------------------------------- */
  const [searchTerm, setSearchTerm] = useState(''); // 선호 챔피언 검색어
  const [bannedSearchTerm, setBannedSearchTerm] = useState(''); // 비선호 챔피언 검색어

  const [showSuggestions, setShowSuggestions] = useState(false); // 선호 챔피언 추천박스
  const [showBannedSuggestions, setShowBannedSuggestions] = useState(false);  // 비선호 챔피언 추천박스

  const [selectedIndex, setSelectedIndex] = useState(0); // 선호 챔피언 추천박스 활성 인덱스
  const [bannedSelectedIndex, setBannedSelectedIndex] = useState(0); // 비선호 챔피언 추천박스 활성 인덱스

  /* ----- ref: DOM 요소 참조 ---------------------------------------------- */
  const searchRef = useRef(null); // 선호 입력창 wrapper
  const bannedSearchRef = useRef(null); // 비선호 입력창 wrapper
  const preferredSuggestionsRef = useRef(null); // 선호 챔피언 추천 목록
  const bannedSuggestionsRef = useRef(null); // 비선호 챔피언 추천 목록

  /* ------------------ effect: 외부 클릭 시 추천 박스 닫기 ------------------ */
  useEffect(() => {
    /** 외부 클릭을 감지해 추천 박스를 닫는 함수. */
    const handleClickOutside = (event) => {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setShowSuggestions(false);
      }
      if (bannedSearchRef.current && !bannedSearchRef.current.contains(event.target)) {
        setShowBannedSuggestions(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  /* ----- effect: 선택 인덱스 변경 시 스크롤 위치 맞추기 ------------------------ */
  useEffect(() => {
    scrollToSelected(selectedIndex, 'preferred');
  }, [selectedIndex]);

  useEffect(() => {
    scrollToSelected(bannedSelectedIndex, 'banned');
  }, [bannedSelectedIndex]);

  /* ---------------- 입력/포커스 핸들러 ----------------------------------- */

  /** 검색어가 변경될 때 상태를 업데이트하는 함수. */
  const handleChange = (e, kind) => {
    if (kind === 'preferred') {
      setSearchTerm(e.target.value);
      setShowSuggestions(true);
      setSelectedIndex(0);
    } else {
      setBannedSearchTerm(e.target.value);
      setShowBannedSuggestions(true);
      setBannedSelectedIndex(0);
    }
  };

  /** 입력창 포커스 시 추천 박스를 여는 함수. */
  const handleShowSuggestions = (kind) => {
    if (kind === 'preferred') {
      setShowSuggestions(true);
    } else {
      setShowBannedSuggestions(true);
    }
  }

  /** 선택된 추천 항목이 보이도록 스크롤을 이동하는 함수.*/
  const scrollToSelected = (index, type) => {
    const suggestionsElement = type === 'preferred' ?
      preferredSuggestionsRef.current :
      bannedSuggestionsRef.current;

    if (!suggestionsElement) return;

    const selectedElement = suggestionsElement.children[index];
    if (!selectedElement) return;

    const containerHeight = suggestionsElement.clientHeight;
    const elementHeight = selectedElement.clientHeight;
    const elementTop = selectedElement.offsetTop;
    const currentScroll = suggestionsElement.scrollTop;

    // 선택된 항목이 컨테이너 아래에 있을 때
    if (elementTop + elementHeight > currentScroll + containerHeight) {
      suggestionsElement.scrollTop = elementTop + elementHeight - containerHeight;
    }
    // 선택된 항목이 컨테이너 위에 있을 때
    else if (elementTop < currentScroll) {
      suggestionsElement.scrollTop = elementTop;
    }
  };

  /** 키보드 입력(▲▼Enter)을 처리하는 함수. */
  const handleKeyDown = (e, type) => {
    const suggestions = filterChampions(type === 'preferred' ? searchTerm : bannedSearchTerm);
    const currentIndex = type === 'preferred' ? selectedIndex : bannedSelectedIndex;
    const setIndex = type === 'preferred' ? setSelectedIndex : setBannedSelectedIndex;

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        if (suggestions.length > 0) {
          setIndex((prevIndex) =>
            prevIndex < suggestions.length - 1 ? prevIndex + 1 : prevIndex
          );
        }
        break;
      case 'ArrowUp':
        e.preventDefault();
        if (suggestions.length > 0) {
          setIndex((prevIndex) =>
            prevIndex > 0 ? prevIndex - 1 : prevIndex
          );
        }
        break;
      case 'Enter':
        e.preventDefault();
        if (suggestions.length > 0) {
          handleChampionSelect(suggestions[currentIndex], type);
        }
        break;
      default:
        break;
    }
  };

  /** 검색어로 챔피언 배열을 필터링하는 함수. */
  const filterChampions = (term) => {
    if (!term) return champions;
    const lowerTerm = term.toLowerCase();
    return champions.filter(champion =>
      champion.name.toLowerCase().includes(lowerTerm) ||
      champion.id.toLowerCase().includes(lowerTerm)
    );
  };

  /** 챔피언을 선택하여 상태에 반영하는 함수. */
  const handleChampionSelect = (champion, type) => {
    const key = type === 'preferred' ? 'preferredChampions' : 'bannedChampions';
    const otherKey = type === 'preferred' ? 'bannedChampions' : 'preferredChampions';
    const currentChampions = [...(matchingCriteria?.selectedChampions?.[key] || [])];
    const otherChampions = [...(matchingCriteria?.selectedChampions?.[otherKey] || [])];

    // 1. 이미 선택된 경우(같은 리스트) 중복 방지
    if (currentChampions.some(c => c.id === champion.id)) {
      alert('이미 선택한 챔피언입니다.');
      return;
    }

    // 2. 다른 리스트에 이미 있으면 추가 불가
    if (otherChampions.some(c => c.id === champion.id)) {
      alert('동일한 챔피언이 선택되었습니다. 다시 확인해주세요.');
      return;
    }

    const newCriteria = {
      ...matchingCriteria,
      selectedChampions: {
        ...matchingCriteria?.selectedChampions,
        [key]: [...currentChampions, champion]
      }
    };

    setMatchingCriteria(newCriteria);

    if (type === 'preferred') {
      setSearchTerm('');
      setShowSuggestions(false);
    } else {
      setBannedSearchTerm('');
      setShowBannedSuggestions(false);
    }
  };

  /** 선택된 챔피언 태그를 제거하는 함수. */
  const handleRemoveChampion = (championId, type) => {
    const key = type === 'preferred' ? 'preferredChampions' : 'bannedChampions';

    const newCriteria = {
      ...matchingCriteria,
      selectedChampions: {
        ...matchingCriteria.selectedChampions,
        [key]: matchingCriteria.selectedChampions[key].filter(c => c.id !== championId)
      }
    };

    setMatchingCriteria(newCriteria);
  };


  /* ---------------- 렌더링: 라인 선택 모드 --------------------------------- */

  if (type === 'line') {
    return (
      <Section>
        <Label>{kind === 'my' ? '라인 선택' : '상대 라인 선택'}</Label>
        <LaneGroup>
          {lines.map(line => (
            <LaneButton
              key={`${kind}-${line}`}
              type="button"
              selected={selectedValue === line}
              disabled={disabledValue === line}
              onClick={() => {
                handleLineSelect(
                  kind === 'my' ? 'myLine' : 'partnerLine',
                  line,
                  matchingCriteria,
                  setMatchingCriteria
                )
              }}
            >
              {line}
            </LaneButton>
          ))}
        </LaneGroup>
      </Section>
    );
  } else if (type === 'champion') {

    /* ---------------- 렌더링: 챔피언 선택 모드 ------------------------------- */

    return (
      <Section>
        <Label>{kind === 'preferred' ? '선호 챔피언' : '비선호 챔피언'}</Label>
        <SearchContainer ref={kind === 'preferred' ? searchRef : bannedSearchRef}>
          {/* 검색 입력 */}
          <SearchInput>
            <input
              type="text"
              placeholder="챔피언 검색..."
              value={kind === 'preferred' ? searchTerm : bannedSearchTerm}
              onChange={(e) => {
                handleChange(e, kind);
              }}
              onFocus={() => handleShowSuggestions(kind)}
              onKeyDown={(e) => handleKeyDown(e, kind)}
            />
          </SearchInput>

          {/* 추천 목록 */}
          {(() => {
            const {
              show,
              term,
              index,
              ref
            } = kind === 'preferred' // 'preferred' 또는 'banned'
              ? {
                show: showSuggestions,
                term: searchTerm,
                index: selectedIndex,
                ref: preferredSuggestionsRef
              }
              : {
                show: showBannedSuggestions,
                term: bannedSearchTerm,
                index: bannedSelectedIndex,
                ref: bannedSuggestionsRef
              };

            return show && (
              <Suggestions ref={ref}>
                {filterChampions(term).map((champion, idx) => (
                  <SuggestionItem
                    key={champion.id}
                    onClick={() => handleChampionSelect(champion, kind)}
                    selected={idx === index}
                  >
                    <img src={`/champions/${champion.image}`} alt={champion.name} />
                    {champion.name}
                  </SuggestionItem>
                ))}
              </Suggestions>
            );
          })()}

          {/* 선택된 챔피언 태그 */}
          <ChampionTags>
            {(kind === 'preferred'
                ? matchingCriteria.selectedChampions.preferredChampions
                : matchingCriteria.selectedChampions.bannedChampions
            ).map(champion => (
              <ChampionTag key={champion.id}>
                <img src={`/champions/${champion.image}`} alt={champion.name} />
                {champion.name}
                <button
                  type="button"
                  onClick={() => handleRemoveChampion(champion.id, kind)}
                >
                  ×
                </button>
              </ChampionTag>
            ))}
          </ChampionTags>
        </SearchContainer>
      </Section>
    );
  } else if (type === 'userPreferenceText'){

    /* ---------------- 렌더링: 사용자 메모 입력 ------------------------------ */

    return (
        <Section>
          <Label>원하는 상대의 플레이 스타일</Label>
          <TextInput
              type="text"
              maxLength={TEXT_LIMIT}
              placeholder="ex) 탱커를 잘함, 로밍을 잘감, ..."
              value={matchingCriteria.userPreferenceText || ''}
              onChange={(e) =>
                  setMatchingCriteria({
                    ...matchingCriteria,
                    userPreferenceText: e.target.value.slice(0, TEXT_LIMIT),
                  })
              }
          />
        </Section>
    );
  }

  return null;  // 방어 코드
};

export default SectionSelector;

/* ------------- 스타일 컴포넌트 --------------------------------------------- */

const Section = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
`;

const Label = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: white;
  font-size: 0.9rem;
`;

const LaneGroup = styled.div`
  display: flex;
  gap: 0.5rem;
`;

const LaneButton = styled.button`
  flex: 1;
  padding: 0.5rem;
  border: none;
  background: ${props => props.selected ? '#FF416C' : props.disabled ? 'rgba(255, 255, 255, 0.05)' : 'rgba(255, 255, 255, 0.1)'};
  color: ${props => props.disabled ? 'rgba(255, 255, 255, 0.3)' : 'white'};
  border-radius: 4px;
  cursor: ${props => props.disabled ? 'not-allowed' : 'pointer'};
  font-size: 0.9rem;
  transition: all 0.2s;

  &:hover {
    background: ${props => props.selected ? '#FF416C' : props.disabled ? 'rgba(255, 255, 255, 0.05)' : 'rgba(255, 255, 255, 0.2)'};
  }
`;

const SearchContainer = styled.div`
  position: relative;
`;

const SearchInput = styled.div`
  position: relative;
  
  input {
    width: 100%;
    padding: 0.5rem 2rem 0.5rem 0.8rem;
    background: rgba(255, 255, 255, 0.1);
    border: none;
    border-radius: 4px;
    color: white;
    font-size: 0.9rem;

    &::placeholder {
      color: rgba(255, 255, 255, 0.5);
    }

    &:focus {
      outline: none;
    }
  }

  &::after {
    content: '';
    position: absolute;
    right: 0.8rem;
    top: 50%;
    transform: translateY(-50%);
    width: 14px;
    height: 14px;
    background-image: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" fill="white" viewBox="0 0 24 24"><path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/></svg>');
    background-size: contain;
    background-repeat: no-repeat;
    opacity: 0.5;
  }
`;

const Suggestions = styled.div`
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #1a1a1a;
  border-radius: 4px;
  margin-top: 0.5rem;
  max-height: 200px;
  overflow-y: auto;
  z-index: 10;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
`;

const SuggestionItem = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  cursor: pointer;
  color: white;
  transition: background-color 0.2s;
  background: ${props => props.selected ? 'rgba(255, 255, 255, 0.1)' : 'transparent'};

  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }

  img {
    width: 24px;
    height: 24px;
    border-radius: 12px;
  }
`;

const ChampionTags = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.5rem;
`;

const ChampionTag = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  color: white;
  font-size: 0.9rem;

  img {
    width: 20px;
    height: 20px;
    border-radius: 10px;
  }

  button {
    background: none;
    border: none;
    color: rgba(255, 255, 255, 0.7);
    cursor: pointer;
    padding: 0;
    font-size: 1.2rem;
    line-height: 1;

    &:hover {
      color: white;
    }
  }
`;

const TextInput = styled.input`
  width: 100%;
  padding: 0.5rem 0.8rem;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 4px;
  color: white;
  font-size: 0.9rem;
  &::placeholder { color: rgba(255, 255, 255, 0.5); }
  &:focus { outline: none; }
`;
