export type Scenario = {
  id: number;
  name: string;
  runs: Run[]
};

export type Evaluation = {
  id: number;
  success: boolean;
  errorMessage: string;
}

export type Assertion = {
  id: number;
  identifier: string;
  assertType: string;
  assertExpression:  string;
  evaluations: Evaluation[];
}

export type Step = {
  id: number;
  name: string;
  identifier:  string;
  assertions: Assertion[];
}

export type Run = {
  id: number;
  name: string;
  scenarioId: number;
  scenarioName: string;
  steps: Step[]
}


