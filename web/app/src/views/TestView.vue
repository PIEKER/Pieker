<script setup lang="ts">
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Doughnut } from 'vue-chartjs'
import { computed, onMounted, ref } from 'vue'
import api from '@/js/api'

ChartJS.register(ArcElement, Tooltip, Legend)
ChartJS.defaults.borderColor = '#D6D6D6'

const allRuns = ref([])
const currentRun = ref({})
const currentSuccess = ref([])
const currentFailure = ref([])
const options = {
  responsive: true,
}

const getAllRuns = async () => {
  const response = await api.get('/runs/all')
  allRuns.value = response.data
  if (allRuns.value.length > 0) {
    currentRun.value = allRuns.value[0]
    computeChartData()
  }
}

const getRun = async (id: number) => {
  const response = await api.get(`/runs/${id}`)
  currentRun.value = response.data
  currentSuccess.value = []
  currentFailure.value = []
  computeChartData()
}

const computeChartData = () => {
  const evaluations = currentRun.value.evaluations
  for (const evaluation of evaluations) {
    if (evaluation.success) {
      currentSuccess.value.push(evaluation)
    } else {
      currentFailure.value.push(evaluation)
    }
  }
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
  <div class="list-wrapper">
    <v-list lines="two">
      <v-list-subheader>Ausführungen</v-list-subheader>
      <v-list-item v-for="run in allRuns" :key="run.id" :title="run.name">
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
  <div class="chart-wrapper d-flex flex-column justify-center">
    <div class="run-header">
      <h2>Run: {{ currentRun.id }}</h2>
    </div>
    <div class="w-100 d-flex flex-row justify-center">
      <div class="chart-item">
        <Doughnut :data="chartData" :options="options" />
      </div>
    </div>
    <div class="evaluation">
      <v-list lines="two">
        <v-list-item
          v-for="ev in currentRun.evaluations"
          :key="ev.id"
          :title="ev.assertExpression"
          :subtitle="ev.assertType"
        >
          <template v-slot:append>
            <v-avatar :class="[ev.success ? 'success' : 'error']">
              <v-icon v-if="ev.success" color="white">mdi-check</v-icon>
              <v-icon v-else color="white">mdi-close</v-icon>
            </v-avatar>
          </template>
        </v-list-item>
      </v-list>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '../assets/colors';
.v-avatar {
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

.list-wrapper {
  width: 60%;
  position: absolute;
  left: 0;
  top: 50px;
  bottom: 0;
  box-shadow: 0 0 10px;
  overflow: auto;
}

.chart-wrapper {
  .run-header {
    h2 {
      font-size: 1.5rem;
      color: colors.$primary;
      width: 100%;
    }
  }

  .chart-item {
    margin-top: 5%;
    width: 50%!important;;
  }

  .evaluation {
    margin-top: 5%;
    overflow: auto;
    .success {
      background-color: #41b883 !important;
    }
    .error {
      background-color: #dd1b16 !important;
    }
  }
  width: 40%;
  height: 80%;
  position: absolute;
  right: 0;
  top: 50px;
  padding: 20px;
}
</style>
