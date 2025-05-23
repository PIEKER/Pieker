<script setup lang="ts">
import { ref, toRef, watch } from 'vue'

const props = defineProps([
  'modelValue',
  'viewVariableForm',
  'viewVariableList',
  'allFixed',
  'tableLabel',
])
const emit = defineEmits([
  'update:modelValue',
  'remove-variable',
  'changeViewVariableForm',
  'changeViewVariableList',
])

const localVariables = toRef(props, 'modelValue')
const variableName = ref(null)
const variableValue = ref(null)

// Remove a step by index
const removeVariable = (index: number) => {
  emit('remove-variable', index)
}

const addVariable = () => {
  localVariables.value.push({
    name: variableName.value,
    value: variableValue.value,
  })
  emit('changeViewVariableForm')
}

const changeViewVariableForm = () => {
  emit('changeViewVariableForm')
}

const changeViewVariableList = () => {
  emit('changeViewVariableList')
}

// Watch for local changes and emit back to parent
watch(
  localVariables,
  (newVal) => {
    emit('update:modelValue', newVal)
  },
  { deep: true },
)
</script>

<template>
  <div
    v-if="viewVariableForm"
    :class="[
      { 'view-blocker-absolute': !allFixed },
      { 'view-blocker-fixed': allFixed },
    ]"
    class="d-flex flex-row justify-center p-4"
  >
    <div class="variable-container d-flex flex-column w-50 bg-light p-3 border">
      <div class="d-flex flex-row mb-2">
        <div class="d-flex flex-column justify-center mr-auto">
          <span>New Variable </span>
        </div>
        <v-btn color="success" @click="addVariable">
          <v-icon icon="mdi-check"></v-icon>
        </v-btn>
        <v-btn class="ml-2" color="error" @click="changeViewVariableForm">
          <v-icon icon="mdi-close"></v-icon>
        </v-btn>
      </div>
      <v-text-field
        v-model="variableName"
        :rules="[(value) => !!value || 'Required.']"
        label="Key"
      />
      <v-text-field
        v-model="variableValue"
        :rules="[(value) => !!value || 'Required.']"
        label="Value"
      />
    </div>
  </div>

  <div v-if="viewVariableList" class="view-blocker-fixed d-flex flex-row justify-center">
    <div class="d-flex flex-column w-75 bg-light p-3 border">
      <div class="d-flex flex-row mb-2 fixed">
        <div class="d-flex flex-column justify-center mr-auto">
          <span>{{tableLabel}} Variables </span>
        </div>
        <v-btn class="ml-2" color="error" @click="changeViewVariableList">
          <v-icon icon="mdi-close"></v-icon>
        </v-btn>
      </div>
      <v-table class="shadow mt-4 h-75 w-100" fixed-header>
        <thead class="fixed">
          <tr>
            <th class="text-left">Name</th>
            <th class="text-left">Value</th>
            <th />
          </tr>
        </thead>
        <tbody>
          <tr v-for="(v, index) in localVariables" :key="v.name">
            <td>{{ v.name }}</td>
            <td>{{ v.value }}</td>
            <td style="width: 5%">
              <v-btn color="error" @click="removeVariable(index)">
                <v-icon icon="mdi-delete" />
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>
  </div>
</template>

<style scoped lang="scss">
.view-blocker-absolute {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 99;
}

.view-blocker-fixed {
  position: fixed;
  padding: 10vh;
  top: 5vh;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 99;
}

.variable-container {
  max-height: 25vh;
}
</style>
