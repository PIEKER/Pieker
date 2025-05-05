<script setup lang="ts">
import { watch, toRef, defineProps, defineEmits, ref } from 'vue'
import ScenarioEdit from '@/components/ScenarioEdit.vue'
import VariableEdit from '@/components/VariableEdit.vue'

const props = defineProps(['config'])
const emit = defineEmits(['update:config'])

const localConfig = toRef(props, 'config')
const viewVariableForm = ref(false)
const viewVariableList = ref(false)

const changeViewVariableForm = () => {
  viewVariableForm.value = !viewVariableForm.value
}

const changeViewVariableList = () => {
  viewVariableList.value = !viewVariableList.value
}

const removeVariable = (index: number) => {
  localConfig.value.variables.splice(index, 1)
}


const addScenario = () => {
  localConfig.value.scenarios.push({
    name: '',
    doc: '',
    variables: [{name: '', value: ''}],
    beforeEach: null,
    steps: [],
  })
}

const updateScenario = (index:number, updated) => {
  localConfig.value.scenarios[index] = updated
}

const removeScenario = (index:number) => {
  localConfig.value.scenarios.splice(index, 1)
}

watch(localConfig, (newVal) => {
  emit('update:config', newVal)
}, { deep: true })
</script>

<template>
  <div>
    <!-- Feature Block -->
    <h5>Feature</h5>
    <div class="d-flex flex-row">
      <div class="mb-3 w-25 mr-auto" >
        <v-text-field v-model="localConfig.feature.name" placeholder="Name"/>
      </div>

      <div class="mb-3 w-50">
        <v-textarea v-model="localConfig.feature.doc" rows="1" placeholder="Docs"/>
      </div>
    </div>
    <!-- Button -->
    <div class="d-flex flex-row justify-content-evenly">
      <v-btn class="btn-pieker mb-5" @click="addScenario">
        <v-icon icon="mdi-plus" start /> Add Test-Plan
      </v-btn>
      <v-btn class="btn-pieker mb-5" @click="changeViewVariableForm" :disabled="viewVariableForm">
        <v-icon icon="mdi-plus" start /> Add Variable
      </v-btn>
      <v-btn class="btn-pieker mb-5" @click="changeViewVariableList" :disabled="viewVariableList || localConfig.variables.length==0">
        <v-icon icon="mdi-eye-outline" start /> Show Variables
      </v-btn>
    </div>

    <!-- Variables -->
    <VariableEdit :modelValue="localConfig.variables"
                  :viewVariableForm="viewVariableForm"
                  :viewVariableList="viewVariableList"
                  :allFixed="true"
                  tableLabel="Feature"
                  @changeViewVariableForm="changeViewVariableForm"
                  @changeViewVariableList="changeViewVariableList"
                  @removeVariable="removeVariable"

    />

    <!-- Scenario Blocks -->
    <v-expansion-panels>
        <ScenarioEdit v-for="(scenario, index) in localConfig.scenarios"
                      :key="index"
                      :modelValue="scenario"
                      @update:modelValue="updateScenario(index, $event)"
                      @remove-scenario="removeScenario(index)"
        />
    </v-expansion-panels>

  </div>
</template>

<style scoped lang="scss">

</style>
