<script setup lang="ts">
import { ref, toRef, watch } from 'vue'
import StepEdit from '@/components/StepEdit.vue'

const props = defineProps(['modelValue', 'isBeforeEach'])
const emit = defineEmits(['update:modelValue', 'remove-step'])
const stepEditView = ref(false)

const localStep = toRef(props, 'modelValue')

// Remove a step by index
const removeStep = () => {
  emit('remove-step')
}

const updateStepEditView = () => {
  stepEditView.value = !stepEditView.value
}

// Watch for local changes and emit back to parent
watch(localStep, (newVal) => {
  emit('update:modelValue', newVal)
}, { deep: true })

</script>

<template>
  <v-expansion-panel>
    <v-expansion-panel-title>
      <template v-slot:default="{}">
        <v-row no-gutters class="w-100 align-center">
          <v-col v-if="isBeforeEach" class="d-flex justify-start" cols="6">
            <strong>BeforeEach: </strong>  {{ localStep.name || '(Unnamed Step)' }}
          </v-col>
          <v-col v-else class="d-flex justify-start" cols="6">
            <strong>Test: </strong>  {{ localStep.name || '(Unnamed Step)' }}
          </v-col>
          <v-col class="d-flex justify-end" cols="6">
            <v-btn size="x-small" icon @click.stop="updateStepEditView">
              <v-icon icon="mdi-pencil" />
            </v-btn>
            <v-btn size="x-small" color="error" icon @click.stop="removeStep">
              <v-icon icon="mdi-delete" />
            </v-btn>
          </v-col>
        </v-row>
      </template>
    </v-expansion-panel-title>

    <v-expansion-panel-text>
      <div class="d-flex flex-row">
        <div class="mb-3 w-25 mr-auto">
          <v-text-field v-model="localStep.name" placeholder="Step Name" />
        </div>
        <div class="mb-3 w-50">
          <v-textarea v-model="localStep.doc" rows="1" placeholder="Step Docs" />
        </div>
      </div>
    </v-expansion-panel-text>
  </v-expansion-panel>

  <StepEdit v-if="stepEditView"
            :modelValue="localStep"
            @closeStepEdit="updateStepEditView"/>
</template>

<style scoped lang="scss">
  .v-col .v-btn{
    margin-left: 1rem;
  }
</style>
