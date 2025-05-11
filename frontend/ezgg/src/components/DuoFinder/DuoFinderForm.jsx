import React, { useState, useRef, useEffect } from 'react';
import styled from '@emotion/styled';
import { champions } from '../../data/champions';

const lines = ['TOP', 'JUG', 'MID', 'AD', 'SUP'];
const lineMap = {
  TOP: 'TOP',
  JUG: 'JUNGLE',
  MID: 'MIDDLE',
  AD: 'BOTTOM',
  SUP: 'UTILITY',
};

const DuoFinderForm = (
  {
    matchingCriteria,
    setMatchingCriteria,
  }) => {

  const [searchTerm, setSearchTerm] = useState('');
  const [bannedSearchTerm, setBannedSearchTerm] = useState('');
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [showBannedSuggestions, setShowBannedSuggestions] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [bannedSelectedIndex, setBannedSelectedIndex] = useState(0);
  const searchRef = useRef(null);
  const bannedSearchRef = useRef(null);
  const preferredSuggestionsRef = useRef(null);
  const bannedSuggestionsRef = useRef(null);

  useEffect(() => {
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

  useEffect(() => {
    scrollToSelected(selectedIndex, 'preferred');
  }, [selectedIndex]);

  useEffect(() => {
    scrollToSelected(bannedSelectedIndex, 'banned');
  }, [bannedSelectedIndex]);

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

  const handleLineSelect = (type, line) => {
    const currentWantLine = matchingCriteria?.wantLine || {};
    const currentMyLine = currentWantLine.myLine || '';
    const currentPartnerLine = currentWantLine.partnerLine || '';

    if (type === 'partnerLine' && line === currentMyLine) return;
    if (type === 'myLine' && line === currentPartnerLine) return;

    const newWantLine = {
      ...currentWantLine,
      [type]: currentWantLine[type] === line ? '' : line
    };

    setMatchingCriteria({
      ...matchingCriteria,
      wantLine: newWantLine
    });
  };

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

  const filterChampions = (term) => {
    if (!term) return champions;
    const lowerTerm = term.toLowerCase();
    return champions.filter(champion => 
      champion.name.toLowerCase().includes(lowerTerm) ||
      champion.id.toLowerCase().includes(lowerTerm)
    );
  };

  const handleChampionSelect = (champion, type) => {
    const key = type === 'preferred' ? 'preferredChampions' : 'bannedChampions';
    const otherKey = type === 'preferred' ? 'bannedChampions' : 'preferredChampions';
    const currentChampions = [...(matchingCriteria?.selectedChampions?.[key] || [])];
    const otherChampions = [...(matchingCriteria?.selectedChampions?.[otherKey] || [])];

    // 1. 이미 선택된 경우(같은 리스트) 중복 방지
    if (currentChampions.some(c => c.id === champion.id)) return;

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

  // const handleSubmit = async (e) => {
  //   e.preventDefault();
  //   if (!isFormValid()) return;
  //
  //   const payload = {
  //     wantLine: {
  //       myLine: formData.preferredLane,
  //       partnerLine: formData.partnerLane,
  //     },
  //     championInfo: {
  //       preferredChampion: formData.preferredChampions.length > 0 ? formData.preferredChampions[0].id : "",
  //       unpreferredChampion: formData.bannedChampions.length > 0 ? formData.bannedChampions[0].id : ""
  //     }
  //   };
  //
  //   connect(() => {
  //     console.log("[DuoFinderForm]\n>>> 연결 후 매칭 요청 전송")
  //     setIsMatching(true);
  //     sendMatchingRequest(payload);
  //     onSubmit(payload);
  //   })
  //   // } catch (err) {
  //   //   console.error("웹소켓 연결 실패:", err);
  //   //   setIsMatching(false);
  //   // }
  // }

  return (
    <Form>
      <Section>
        <Label>라인 선택</Label>
        <LaneGroup>
          {lines.map(line => (
            <LaneButton
              key={`my-${line}`}
              type="button"
              selected={matchingCriteria.wantLine.myLine === line}
              disabled={line === matchingCriteria.wantLine.partnerLine}
              onClick={() => handleLineSelect('myLine', line)}
            >
              {line}
            </LaneButton>
          ))}
        </LaneGroup>
      </Section>

      <Section>
        <Label>상대 선택</Label>
        <LaneGroup>
          {lines.map(line => (
            <LaneButton
              key={`partner-${line}`}
              type="button"
              selected={matchingCriteria.wantLine.partnerLine === line}
              disabled={line === matchingCriteria.wantLine.myLine}
              onClick={() => handleLineSelect('partnerLine', line)}
            >
              {line}
            </LaneButton>
          ))}
        </LaneGroup>
      </Section>

      <Section>
        <Label>선호 챔피언</Label>
        <SearchContainer ref={searchRef}>
          <SearchInput>
            <input
              type="text"
              placeholder="챔피언 검색..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setShowSuggestions(true);
                setSelectedIndex(0);
              }}
              onFocus={() => setShowSuggestions(true)}
              onKeyDown={(e) => handleKeyDown(e, 'preferred')}
            />
          </SearchInput>
          {showSuggestions && (
            <Suggestions ref={preferredSuggestionsRef}>
              {filterChampions(searchTerm).map((champion, index) => (
                <SuggestionItem
                  key={champion.id}
                  onClick={() => handleChampionSelect(champion, 'preferred')}
                  selected={index === selectedIndex}
                >
                  <img src={`/champions/${champion.image}`} alt={champion.name} />
                  {champion.name}
                </SuggestionItem>
              ))}
            </Suggestions>
          )}
          <ChampionTags>
            {matchingCriteria.selectedChampions.preferredChampions.map(champion => (
              <ChampionTag key={champion.id}>
                <img src={`/champions/${champion.image}`} alt={champion.name} />
                {champion.name}
                <button
                  type="button"
                  onClick={() => handleRemoveChampion(champion.id, 'preferred')}
                >
                  ×
                </button>
              </ChampionTag>
            ))}
          </ChampionTags>
        </SearchContainer>
      </Section>

      <Section>
        <Label>비선호 챔피언</Label>
        <SearchContainer ref={bannedSearchRef}>
          <SearchInput>
            <input
              type="text"
              placeholder="챔피언 검색..."
              value={bannedSearchTerm}
              onChange={(e) => {
                setBannedSearchTerm(e.target.value);
                setShowBannedSuggestions(true);
                setBannedSelectedIndex(0);
              }}
              onFocus={() => setShowBannedSuggestions(true)}
              onKeyDown={(e) => handleKeyDown(e, 'banned')}
            />
          </SearchInput>
          {showBannedSuggestions && (
            <Suggestions ref={bannedSuggestionsRef}>
              {filterChampions(bannedSearchTerm).map((champion, index) => (
                <SuggestionItem
                  key={champion.id}
                  onClick={() => handleChampionSelect(champion, 'banned')}
                  selected={index === bannedSelectedIndex}
                >
                  <img src={`/champions/${champion.image}`} alt={champion.name} />
                  {champion.name}
                </SuggestionItem>
              ))}
            </Suggestions>
          )}
          <ChampionTags>
            {matchingCriteria.selectedChampions.bannedChampions.map(champion => (
              <ChampionTag key={champion.id}>
                <img src={`/champions/${champion.image}`} alt={champion.name} />
                {champion.name}
                <button
                  type="button"
                  onClick={() => handleRemoveChampion(champion.id, 'banned')}
                >
                  ×
                </button>
              </ChampionTag>
            ))}
          </ChampionTags>
        </SearchContainer>
      </Section>
    </Form>
  );
};

export default DuoFinderForm;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`;

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
