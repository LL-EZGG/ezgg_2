import MatchResult from "./DuoFinder/MatchResult.jsx";
import DuoFinderForm from "./DuoFinder/DuoFinderForm.jsx";
import styled from '@emotion/styled';

export const MatchingInterface = (
  {
    matchResult,
    matchingCriteria,
    setMatchingCriteria,
  }) => (
  <FormContainer>
    {matchResult ? (
      <MatchResult
        criteria={matchingCriteria}
        matchResult={matchResult}
      />
    ) : (
      <DuoFinderForm
        matchingCriteria={matchingCriteria}
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
