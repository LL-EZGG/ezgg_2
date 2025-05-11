export const isValidCriteria = (criteria) => {
  if (!criteria || !isValidLine(criteria)) return false;

  // 확장 포인트: 추가 필수 조건
  // if (criteria.새로운조건 !== requiredValue) return false;

  return true;
};

const isValidLine = (criteria) => {
  return criteria?.wantLine?.myLine &&
    criteria?.wantLine?.partnerLine &&
    criteria.wantLine.myLine !== criteria.wantLine.partnerLine;
}
