<script setup lang="ts">
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Doughnut } from 'vue-chartjs'
import { computed, onMounted, ref } from 'vue'
import api from '@/js/api'

ChartJS.register(ArcElement, Tooltip, Legend)
ChartJS.defaults.borderColor = '#D6D6D6'

const scenario = ref({})
const currentRun = ref({})
const currentStep = ref({})
const currentSuccess = ref([])
const currentFailure = ref([])
const options = {
  responsive: true,
}

const getAllRuns = async () => {
  const response = await api.get('/scenarios/1')
  scenario.value = response.data
  if (scenario.value.runs.length > 0) {
    currentRun.value = scenario.value.runs[0]
    computeChartData()
  }
}

const getRun = async (id: number) => {
  const response = await api.get(`/runs/${id}`)
  currentRun.value = response.data
  currentSuccess.value = []
  currentFailure.value = []
  computeChartData()
  currentStep.value = {}
}

const computeChartData = () => {
  const steps = currentRun.value.steps
  for (const step of steps) {
    for (const evaluation of step.evaluations) {
      if (evaluation.success) {
        currentSuccess.value.push(evaluation)
      } else {
        currentFailure.value.push(evaluation)
      }
    }
  }
}

const setCurrentStep = (step) => {
  currentStep.value = step
}

const hasPassed = (evaluations) => {
  for (const evaluation of evaluations) {
    if (!evaluation.success) {
      return false
    }
  }
  return true
}

onMounted(() => {
  getAllRuns()
})

// Computed chart data
const chartData = computed(() => ({
  labels: ['Passed', 'Failed'],
  datasets: [
    {
      backgroundColor: ['#41B883', '#DD1B16'],
      data: [currentSuccess.value.length, currentFailure.value.length],
    },
  ],
}))
</script>

<template>
  <div class="test-wrapper row">
    <div class="card-wrapper col">
      <div class="run-header">
        <h2>Ausführungen: {{scenario.name}}</h2>
      </div>
      <div class="card-body">
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
    </div>
    <div class="card-wrapper card-middle col">
      <div class="run-header">
        <h2>Run: {{ currentRun.name }}</h2>
      </div>
      <div class="card-body">
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
            >
              <template v-slot:prepend>
                <v-avatar :class="[hasPassed(step.evaluations) ? 'success' : 'error']">
                  <v-icon v-if="hasPassed(step.evaluations)" color="white">mdi-check</v-icon>
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
    </div>
    <div class="card-wrapper col">
      <div class="run-header">
        <h2>Evaluation: {{currentStep.name}}</h2>
      </div>
      <div class="card-body">
        <v-list lines="two" v-if="currentStep !== null">
          <v-list-item
            v-for="ev in currentStep.evaluations"
            :key="ev.id"
            :title="ev.assertExpression"
            :subtitle="ev.assertType"
          >
            <template v-slot:prepend>
              <v-avatar :class="[ev.success ? 'success' : 'error']">
                <v-icon v-if="ev.success" color="white">mdi-check</v-icon>
                <v-icon v-else color="white">mdi-close</v-icon>
              </v-avatar>

            </template>
          </v-list-item>
        </v-list>
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

.run-header {
  padding: 10px;
  position: absolute;
  z-index: 1;
  h2 {
    font-size: 1.5rem;
    color: colors.$primary;
    width: 100%;
  }
}

.card-wrapper {
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
  margin-top: 50px;
  max-height: 90%;
  overflow: auto;
}
</style>
