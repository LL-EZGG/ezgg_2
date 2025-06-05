// data/keywordEmojis.js
export const keywordEmojis = {
    // Global keywords
    '킬 관여율 높음': '🔥',
    'KDA 높은': '⭐',
    '게임 캐리함': '🎯',
    '딜 잘 넣음': '⚔️',
    '레벨 차이 냄': '📈',
    '스킬 연계 잘함': '🤝',
    '항복 안 함': '💪',
    '생존 잘함': '🛡️',
    '한타 잘함': '⚡',
    '협동 잘함': '🤗',
    '인베이드 잘함': '🗡️',
    '위기 후 승리': '🔄',

    // Laner keywords
    '초반 로밍 적극적': '🏃',
    '라인전 리드함': '👑',
    '포탑 철거 잘함': '🏗️',
    '포탑 다이브 버팀': '🏰',
    '포탑 다이브 잘함': '💥',
    'CS 리드': '💰',
    '첫 킬 만들어냄': '🩸',
    '암살 잘함': '🥷',

    // Jungle keywords
    '시야 장악': '👁️',
    '오브젝트 스틸 잘함': '🎣',
    '카운터 정글 잘함': '🌪️',
    '오브젝트 잘 먹음': '🐲',
    '적 정글 장악': '🌳',
    '전령 활용 잘함': '👹',

    // Support keywords
    '시야 밝혀줌': '💡',
    '팀원 살림': '❤️',
    '킬 도움': '🤲'
};

// 키워드에 이모티콘을 붙여서 반환하는 헬퍼 함수
export const getKeywordWithEmoji = (keywordText) => {
    const emoji = keywordEmojis[keywordText];
    return emoji ? `${emoji} ${keywordText}` : keywordText;
};
