import React from 'react';
import styled from '@emotion/styled';
import {UserProfileCard} from "./user/UserProfileCard.jsx";
import {MatchingInterface} from "./matching/MatchingInterface.jsx";
import ChatRoom from "../Chatting/ChatRoom.jsx";

const DuoFinder = (
    {
        memberDataBundle,
        isLoading,
        userInfo,
        matchingCriteria,
        isMatching,
        setMatchingCriteria,
        matchResult,
        chatMessages,
        sendChatMessage,
        isConnected
    }
) => {
    console.log('[DuoFinder] Props:', {
        matchResult,
        chatMessages,
        isConnected,
        userInfo
    });

    return (
        <Container>
            <UserProfileCard
                userInfo={userInfo}
                memberDataBundle={memberDataBundle}
                isLoading={isLoading}
            />

            {matchResult ? (
                <MatchedContainer>
                    {/* 상대방 카드 */}
                    <UserProfileCard
                        memberDataBundle={matchResult.data}
                        isLoading={isLoading}
                        isOpponent={true}
                    />

                    {/* 채팅방 */}
                    <ChatRoom
                        userInfo={userInfo}
                        matchResult={matchResult}
                        chatMessages={chatMessages}
                        sendChatMessage={sendChatMessage}
                        isConnected={isConnected}
                    />
                </MatchedContainer>
            ) : (
                <MatchingInterface
                    matchResult={matchResult}
                    matchingCriteria={matchingCriteria}
                    isMatching={isMatching}
                    setMatchingCriteria={setMatchingCriteria}
                />
            )}
        </Container>
    )
};

export default DuoFinder;

const Container = styled.div`
    display: flex;
    gap: 2rem;
    padding: 2rem;
    width: 100%;
    max-width: 1400px;
    margin: 0 auto;
    align-items: flex-start;
    justify-content: center;

    @media (max-width: 1024px) {
        width: 95%;
        flex-direction: column;
        align-items: center;
    }
`;

const MatchedContainer = styled.div`
    display: flex;
    flex-direction: column;
    gap: 2rem;
    align-items: center;
`;
