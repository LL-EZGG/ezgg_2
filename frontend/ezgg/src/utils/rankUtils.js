export const getRankImageSrc = (tier) => {
  const tierLower = (tier || "").toLowerCase();
  const validTiers = ["iron", "bronze", "..."]; // 전체 티어 목록

  return validTiers.includes(tierLower)
    ? `/ranks/${tierLower}.png`
    : "/ranks/unranked.png";
};

export const formatTierText = (tier, tierNum) => {
  if (!tier) return "Unranked";
  return tierNum ? `${tier} ${tierNum}` : tier;
};
