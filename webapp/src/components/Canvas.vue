<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps(['config'])
const hasFeatureDoc = computed(() => props.config.feature.doc.trim().length > 0)

</script>

<template>
<pre class="bg-dark text-light p-3 mt-3 h-100 w-100 overflow-auto"><code>
Feature: {{ config.feature.name }}
<template v-if="hasFeatureDoc">
  """
  {{ config.feature.doc }}
  """
</template>
<template v-if="config.variables.length > 0">
    Background:
    <template v-for="(v, index) in config.variables" :key="index">
          @def {{v.name}} = {{v.value}}
    </template>
</template>
<template v-for="(scenario, index) in config.scenarios" :key="index">
    Scenario: {{ scenario.name }}
  <template v-if="scenario.doc.length > 0">
      """
      {{ scenario.doc }}
      """
  </template>
  <template v-for="(v, index) in scenario.variables" :key="index">
        @def {{v.name}} = {{v.value}}
  </template>

  <template v-if="scenario.beforeEach != null" >
      BeforeEach: {{scenario.beforeEach.name}}
          <template v-if="scenario.beforeEach.doc.length > 0">
            """
            {{ scenario.beforeEach.doc }}
            """
          </template>
  </template>
  <template v-for="(step, i) in scenario.steps" :key="i">
        Step: {{step.name}}
        <template v-if="step.doc.length > 0">
          """
          {{ step.doc }}
          """
        </template>
  </template>
</template>
</code></pre>
</template>

<style scoped lang="scss">

</style>
