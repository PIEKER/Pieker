<script setup lang="ts">
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Doughnut } from 'vue-chartjs'
import { computed, onMounted, ref } from 'vue'
import api from '@/ts/api.ts'
import type {Run, Scenario, Step, Assertion} from '@/ts/types.ts'

ChartJS.register(ArcElement, Tooltip, Legend)
ChartJS.defaults.borderColor = '#D6D6D6'
const scenarioList = ref([])
const selectedScenario = ref({id: 0, name: ""})
const scenario = ref<Scenario| null>(null)
const currentRun = ref<Run| null>(null)
const currentStep = ref<Step| null>(null)
const currentSuccess = ref(0)
const currentFailure = ref(0)
const options = {
  responsive: true,
}

const getAllScenarios = async () => {
  const response = await api.get(`/scenarios`)
  scenarioList.value = response.data
}

const getScenario = async (id:number | null) => {
  if (id == null){
    return;
  }
  const response = await api.get(`/scenarios/${id}`)
  scenario.value = response.data
  if (scenario.value != null && scenario.value.runs.length > 0) {
    currentRun.value = scenario.value.runs[0]
    computeChartData()
  }
}

const getRun = async (id: number) => {
  const response = await api.get(`/runs/${id}`)
  currentRun.value = response.data
  currentSuccess.value = 0
  currentFailure.value = 0
  computeChartData()
}

const computeChartData = () => {
  if (currentRun.value == null) {
    return;
  }
  const steps = currentRun.value.steps
  for (const step of steps) {
    if (step.assertions == null){
      continue;
    }
    for (const ass of step.assertions) {
      if (ass.evaluations.length === 0) {
        currentFailure.value += 1;
        return;
      }
      const evaluation = ass.evaluations[0];

      if (evaluation.success) {
        currentSuccess.value += 1;
      } else {
        currentFailure.value += 1;
      }
    }
  }
}

const getAssertionNumber = (step: Step) => {
  if (step.assertions === null) {
    return 0;
  }
  return step.assertions.length;
}

const setCurrentStep = (step: Step) => {
  currentStep.value = step
}

const hasPassed = (assertions: Assertion[]) => {
  if (assertions != null && assertions.length != null) {
    for (const ass of assertions) {
      if (ass.evaluations.length !== 0 && !ass.evaluations[0].success) {
        return false;
      }
    }
  }
  return true
}

onMounted(() => {
  getAllScenarios()
})

// Computed chart data
const chartData = computed(() => ({
  labels: ['Passed', 'Failed'],
  datasets: [
    {
      backgroundColor: ['#41B883', '#DD1B16'],
      data: [currentSuccess.value, currentFailure.value],
    },
  ],
}))
</script>

<template>
  <div class="test-wrapper row">
    <div class="card-wrapper col">
      <div class="run-header">
        <v-select label="Scenario" :items="scenarioList"
                  item-title="name"
                  item-value="id"
                  :v-model="selectedScenario"
                  @update:modelValue="getScenario"
        />
      </div>
      <div class="card-body" v-if="scenario != null">
        <v-list lines="two">
          <v-list-item v-for="run in scenario.runs" :key="run.id" :title="run.name">
            <template v-slot:prepend>
              <v-avatar>
                <v-icon color="white">mdi-test-tube</v-icon>
              </v-avatar>
            </template>

            <template v-slot:append>
              <v-btn icon="mdi-information" variant="text" @click="getRun(run.id)"></v-btn>
            </template>
          </v-list-item>
        </v-list>
      </div>
      <div class="card-body" v-else>
        <p class="container">Select a Test-Scenario for details.</p>
      </div>
    </div>
    <div class="card-wrapper card-middle col">
      <div class="run-header">
        <h2>Run: <span v-if="currentRun != null">{{ currentRun.name }}</span></h2>
      </div>
      <div class="card-body" v-if="currentRun != null">
        <div class="w-100 d-flex flex-row justify-center">
          <div class="chart-item">
            <Doughnut :data="chartData" :options="options" />
          </div>
        </div>
        <div class="step-wrapper">
          <v-list lines="two">
            <v-list-item
              v-for="step in currentRun.steps" :key="step.id"
              :title="step.name"
              :subtitle="'Assertions: ' + getAssertionNumber(step)"
            >
              <template v-slot:prepend>
                <v-avatar :class="[hasPassed(step.assertions) ? 'success' : 'error']">
                  <v-icon v-if="hasPassed(step.assertions)" color="white">mdi-check</v-icon>
                  <v-icon v-else color="white">mdi-close</v-icon>
                </v-avatar>
              </template>
              <template v-slot:append>
                <v-btn icon="mdi-information" variant="text" @click="setCurrentStep(step)"></v-btn>
              </template>
            </v-list-item>
          </v-list>
        </div>
      </div>
      <div class="card-body" v-else>
        <p class="container">Select a Test-Run for details.</p>
      </div>
    </div>
    <div class="card-wrapper col">
      <div class="run-header">
        <h2>Evaluation: <span v-if="currentStep !== null">{{currentStep.name}}</span></h2>
      </div>
      <div class="card-body" v-if="currentStep !== null">
        <v-list lines="two" v-if="currentStep.assertions !== null && currentStep.assertions.length > 0">
          <v-list-item
            v-for="ass in currentStep.assertions"
            :key="ass.id"
            :title="ass.assertExpression"
            :subtitle="ass.assertType"
          >
            <template v-slot:prepend>
              <v-avatar v-if="ass.evaluations.length > 0" :class="[ass.evaluations[0].success ? 'success' : 'error']">
                <v-icon v-if="ass.evaluations[0].success" color="white">mdi-check</v-icon>
                <v-icon v-else color="white">mdi-close</v-icon>
              </v-avatar>
            </template>
          </v-list-item>
        </v-list>
        <p class="container" v-else>Keine Daten gefunden</p>
      </div>
      <div class="card-body" v-else>
        <p class="container">Select a step for details.</p>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '../assets/colors';

.v-avatar{
  background: colors.$primary;
}

.v-list {
  .v-btn--icon {
    color: colors.$primary;
  }
  .v-list-item {
    border-top: 1px solid colors.$secondary-second;
    border-bottom: 1px solid colors.$secondary-second;
  }
}

.test-wrapper {
  margin: 5% 1% 0 1%;
  height: 74vh;
}


.card-wrapper {
  .run-header {
    display: flex;
    flex-direction: row;
    width: 30%;
    padding: 10px;
    position: absolute;
    z-index: 1;
    h2 {
      font-size: 1.5rem;
      color: colors.$primary;
      width: 100%;
    }

    .v-select {
      width: 100%;
    }
  }
  box-shadow: 0 0 10px;
  max-height: 100%;
  overflow: auto;
}

.card-middle{
  margin: 0 1% 0 1%;
}



.chart-item {
  margin-top: 5%;
}

.step-wrapper {
  margin-top: 5%;
  max-height: 30%;
  overflow: auto;
}



.success {
  background-color: #41b883 !important;
}
.error {
  background-color: #dd1b16 !important;
}

.card-body{
  margin-top: 75px;
  max-height: 90%;
  overflow: auto;
}
</style>
