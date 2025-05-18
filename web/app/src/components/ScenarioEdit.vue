<script setup lang="ts">
import { ref, toRef, watch } from 'vue'
import StepPanel from '@/components/StepPanel.vue'
import VariableEdit from '@/components/VariableEdit.vue'

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue', 'remove-scenario'])

const localScenario = toRef(props, 'modelValue')

const viewVariableForm = ref(false)
const viewVariableList = ref(false)

// Watch for local changes and emit back to parent
watch(localScenario, (newVal) => {
  emit('update:modelValue', newVal)
}, { deep: true })

// Add a new empty step
const addStep = () => {
  localScenario.value.steps.push({
    name: '',
    doc: '',
    proxies: [],
    userTraffics: [],
    beforeEach: null
  })
}

const changeViewVariableForm = () => {
  viewVariableForm.value = !viewVariableForm.value
}

const changeViewVariableList = () => {
  viewVariableList.value = !viewVariableList.value
}

const removeVariable = (index: number) => {
  localScenario.value.variables.splice(index, 1)
}
const hasBeforeEach = () => {
  return localScenario.value.beforeEach != null
}

const addBeforeEach = () => {
  localScenario.value.beforeEach = {
    name: '',
    doc: ''
  }
}

const updateBeforeEach = (updated) => {
  localScenario.value.beforeEach = updated
}

const removeBeforeEach = () => {
  localScenario.value.beforeEach = null
}

const updateStep = (index:number, updated) => {
  localScenario.value.steps[index] = updated
}

// Remove a step by index
const removeStep = (index: number) => {
  localScenario.value.steps.splice(index, 1)
}

// Remove the entire scenario (emit a custom event)
const removeScenario = () => {
  emit('remove-scenario')
}
</script>

<template>
  <v-expansion-panel>
    <v-expansion-panel-title>
      <template v-slot:default="{}">
        <v-row no-gutters class="w-100 align-center">
          <v-col class="d-flex justify-start" cols="6">
            <strong>Test-Plan:</strong> {{ localScenario.name || '(Unnamed)' }}
          </v-col>
          <v-col class="d-flex justify-end" cols="6">
            <v-btn size="small" color="error" icon @click.stop="removeScenario">
              <v-icon icon="mdi-delete" />
            </v-btn>
          </v-col>
        </v-row>
      </template>
    </v-expansion-panel-title>

    <v-expansion-panel-text>
      <div class="d-flex flex-row">
        <div class="mb-3 w-25 mr-auto">
          <v-text-field v-model="localScenario.name" placeholder="Name" />
        </div>
        <div class="mb-3 w-50">
          <v-textarea v-model="localScenario.doc" rows="1" placeholder="Docs" />
        </div>
      </div>

      <div class="d-flex flex-row justify-content-evenly">
        <v-btn class="btn-pieker mb-5" @click="addStep">
          <v-icon icon="mdi-plus" start /> Add Test
        </v-btn>
        <v-btn class="btn-pieker mb-5" @click="addBeforeEach" :disabled="hasBeforeEach()">
          <v-icon icon="mdi-plus" start /> Add Before-Each
        </v-btn>
        <v-btn class="btn-pieker mb-5" @click="viewVariableForm= !viewVariableForm" :disabled="viewVariableForm">
          <v-icon icon="mdi-plus" start /> Add Variable
        </v-btn>
        <v-btn class="btn-pieker mb-5" @click="viewVariableList= !viewVariableList" :disabled="viewVariableList || localScenario.variables.length==0">
          <v-icon icon="mdi-eye-outline" start /> Show Variables
        </v-btn>
      </div>

      <VariableEdit :modelValue="localScenario.variables"
                    :viewVariableForm="viewVariableForm"
                    :viewVariableList="viewVariableList"
                    @changeViewVariableForm="changeViewVariableForm"
                    @changeViewVariableList="changeViewVariableList"
                    @removeVariable="removeVariable"

      />

      <v-expansion-panels>
        <StepPanel v-if="hasBeforeEach()"
                  :is-before-each="true"
                  :modelValue="localScenario.beforeEach"
                  @update:modelValue="updateBeforeEach"
                  @remove-step="removeBeforeEach"/>
        <StepPanel v-for="(step, index) in localScenario.steps" :key="index"
                  :modelValue="step"
                  @update:modelValue="updateStep(index, $event)"
                  @remove-step="removeStep(index)"/>
      </v-expansion-panels>
    </v-expansion-panel-text>
  </v-expansion-panel>
</template>

<style scoped lang="scss">
</style>
