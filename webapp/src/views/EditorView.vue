<script setup lang="ts">
import EditPanel from '@/components/EditPanel.vue'
import { ref } from 'vue'
import Canvas from '@/components/Canvas.vue'

const config = ref({
  feature: {
    name: 'Stub Data',
    doc: 'Feature Doc',
  },
  variables: [],
  scenarios: [{
    name: 'Sandbox',
    doc: 'Test-Plan Doc',
    variables: [],
    beforeEach: null,
    steps: [],
  }
  ]
})
</script>

<template>
    <div class="row flex-grow-1">
      <div class="d-flex flex-column">
        <div class="flex-grow-1">
          <div class="p-3 h-100 overflow-auto">
            <EditPanel v-model:config="config" />
          </div>
        </div>

        <div class="canvas-container flex-grow-1 p-2 rounded bg-light shadow">
          <div class="h-100 d-flex justify-content-center align-items-center">
            <Canvas :config="config" />
          </div>
        </div>
      </div>
  </div>
</template>

<style scoped lang="scss">
@use '../assets/colors';

.canvas-container{
  max-height: 50vh;
}

</style>
