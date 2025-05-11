import DuoFinderForm from "./DuoFinder/DuoFinderForm.jsx";
import styled from '@emotion/styled';
import MatchingLoading from "./DuoFinder/MatchingLoading.jsx";

export const MatchingInterface = (
  {
    matchResult,
    matchingCriteria,
    isMatching,
    setMatchingCriteria,
  }) => (
  <FormContainer>
    {isMatching ? (
      <MatchingLoading
        matchingCriteria={matchingCriteria}
        matchResult={matchResult}
      />
    ) : (
      <DuoFinderForm
        matchingCriteria={matchingCriteria}
        isMatching={isMatching}
        setMatchingCriteria={setMatchingCriteria}
      />
    )}
  </FormContainer>
)

const FormContainer = styled.div`
    flex: 1;
    width: 100%;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 12px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    min-height: 635px;
    padding: 2rem;

    @media (max-width: 1024px) {
        max-width: 100%;
    }

    @media (max-width: 768px) {
        padding: 1rem;
    }
`;
