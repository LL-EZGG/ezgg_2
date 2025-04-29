import React, { useState, useRef, useEffect } from 'react';
import styled from '@emotion/styled';
import { champions } from '../../data/champions';
import { useWebSocket } from "../../hooks/useWebSocket.js";

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

const SubmitButton = styled.button`
  padding: 0.8rem;
  background: ${props => props.disabled ? 'rgba(255, 255, 255, 0.3)' : 'white'};
  color: ${props => props.disabled ? 'rgba(0, 0, 0, 0.5)' : 'black'};
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: ${props => props.disabled ? 'not-allowed' : 'pointer'};
  transition: all 0.2s;

  &:hover {
    opacity: ${props => props.disabled ? 1 : 0.9};
  }
`;

const lanes = ['TOP', 'JUG', 'MID', 'AD', 'SUP'];

const DuoFinderForm = ({ onSubmit }) => {
  const [formData, setFormData] = useState({
    preferredLane: '',
    partnerLane: '',
    preferredChampions: [],
    bannedChampions: [],
  });

  const [searchTerm, setSearchTerm] = useState('');
  const [bannedSearchTerm, setBannedSearchTerm] = useState('');
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [showBannedSuggestions, setShowBannedSuggestions] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [bannedSelectedIndex, setBannedSelectedIndex] = useState(0);
  const [isMatching, setIsMatching] = useState(false);
  const [matchResult, setMatchResult] = useState(null);
  const searchRef = useRef(null);
  const bannedSearchRef = useRef(null);
  const preferredSuggestionsRef = useRef(null);
  const bannedSuggestionsRef = useRef(null);

  // WebSocket 메시지 처리 함수
  const handleWebSocketMessage = (response) => {
    console.log("매칭 결과 수신:", response);
    setMatchResult(response);
    setIsMatching(false);
    
    // 매칭 결과가 있으면 부모 컴포넌트에 전달
    if (onSubmit && response) {
      onSubmit(response);
    }
  };

  // WebSocket 연결 설정
  const { sendMessage } = useWebSocket({
    userId: 1,
    onMessage: handleWebSocketMessage,
    onConnect: () => console.log('WebSocket 연결됨'),
    onDisconnect: () => console.log('WebSocket 연결 끊김'),
  });

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

  const isFormValid = () => {
    return (
      formData.preferredLane !== '' &&
      formData.partnerLane !== ''
    );
  };

  const handleLaneSelect = (type, lane) => {
    if (type === 'partnerLane' && lane === formData.preferredLane) {
      return; // 내가 선택한 라인은 상대방이 선택할 수 없음
    }
    if (type === 'preferredLane' && lane === formData.partnerLane) {
      return; // 상대방이 선택한 라인은 내가 선택할 수 없음
    }

    setFormData(prev => ({
      ...prev,
      [type]: prev[type] === lane ? '' : lane
    }));
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
    if (!term) return [];
    const lowerTerm = term.toLowerCase();
    return champions.filter(champion => 
      champion.name.toLowerCase().includes(lowerTerm) ||
      champion.id.toLowerCase().includes(lowerTerm)
    );
  };

  const handleChampionSelect = (champion, type) => {
    const key = type === 'preferred' ? 'preferredChampions' : 'bannedChampions';
    if (!formData[key].find(c => c.id === champion.id)) {
      setFormData(prev => ({
        ...prev,
        [key]: [...prev[key], champion]
      }));
    }
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
    setFormData(prev => ({
      ...prev,
      [key]: prev[key].filter(c => c.id !== championId)
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!isFormValid()) return;
    
    setIsMatching(true);
    
    // WebSocket을 통해 매칭 요청 전송
    try {
      const matchingRequest = {
        userId: 1,
        myLine: formData.preferredLane || '',
        partnerLine: formData.partnerLane || '',
        preferredChampions: formData.preferredChampions.map(champion => champion.id),
        bannedChampions: formData.bannedChampions.map(champion => champion.id)
      };
      
      console.log("매칭 요청 전송:", matchingRequest);
      sendMessage('/app/matching/request', matchingRequest);
      
      // 기존 onSubmit 함수도 호출 (필요한 경우)
      if (onSubmit) {
        onSubmit(formData);
      }
    } catch (error) {
      console.error("매칭 요청 중 오류 발생:", error);
      setIsMatching(false);
    }
  };

  return (
    <Form onSubmit={handleSubmit}>
      <Section>
        <Label>라인 선택</Label>
        <LaneGroup>
          {lanes.map(lane => (
            <LaneButton
              key={`preferred-${lane}`}
              type="button"
              selected={formData.preferredLane === lane}
              disabled={lane === formData.partnerLane}
              onClick={() => handleLaneSelect('preferredLane', lane)}
            >
              {lane}
            </LaneButton>
          ))}
        </LaneGroup>
      </Section>

      <Section>
        <Label>상대 선택</Label>
        <LaneGroup>
          {lanes.map(lane => (
            <LaneButton
              key={`partner-${lane}`}
              type="button"
              selected={formData.partnerLane === lane}
              disabled={lane === formData.preferredLane}
              onClick={() => handleLaneSelect('partnerLane', lane)}
            >
              {lane}
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
          {showSuggestions && searchTerm && (
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
            {formData.preferredChampions.map(champion => (
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
          {showBannedSuggestions && bannedSearchTerm && (
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
            {formData.bannedChampions.map(champion => (
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

      <SubmitButton 
        type="submit"
        disabled={!isFormValid() || isMatching}
      >
        {isMatching ? '매칭 중...' : '매칭 시작'}
      </SubmitButton>
      
      {matchResult && (
        <div style={{ marginTop: '1rem', color: 'white' }}>
          <h3>매칭 결과</h3>
          <p>상대방 ID: {matchResult.partnerId}</p>
          <p>상대방 라인: {matchResult.partnerLine}</p>
        </div>
      )}
    </Form>
  );
};

export default DuoFinderForm; 