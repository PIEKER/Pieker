<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, toRef, watch } from 'vue'

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue', 'remove-proxy'])

const localProxy = toRef(props, 'modelValue')
const viewConditionMenu = ref(false)
const menuWrapper = ref(null)

const currentConditionValue = ref(0)
const currentConditionType = ref('Delay')

const typeList = ['Service', 'Link', 'Database']
const conditionTypeList = ['Delay', 'After', 'Dropout']

const updateConditionMenu = () => {
  viewConditionMenu.value = !viewConditionMenu.value
}

const addCondition = () => {
  localProxy.value.conditions.push({
    type: currentConditionType.value,
    value: currentConditionValue.value
  })
  currentConditionValue.value = 0
  currentConditionType.value = 'Delay'
  viewConditionMenu.value = false
}

const removeProxy = () => {
  emit('remove-proxy')
}

function handleClickOutside(event) {
  if (menuWrapper.value && !menuWrapper.value.contains(event.target) &&
    !event.target.className.includes('v-list-item-title') &&
    !event.target.className.includes('v-list') &&
    !event.target.className.includes('v-list-item')) {
    viewConditionMenu.value = false;
  }
}

const stopPropagation = (event) => {
  event.stopPropagation(); // Prevents event from propagating to parent elements
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

// Watch for local changes and emit back to parent
watch(
  localProxy,
  (newVal) => {
    emit('update:modelValue', newVal)
  },
  { deep: true },
)
</script>

<template>
  <v-expansion-panel>
    <v-expansion-panel-title>
      <template v-slot:default="{}">
        <v-row no-gutters class="w-100 align-center">
          <v-col class="d-flex justify-start" cols="6">
            <strong>Test: </strong>  {{ localProxy.name || '(Unnamed Proxy)' }}
          </v-col>
          <v-col class="d-flex justify-end" cols="6">
            <v-btn size="x-small" color="error" icon @click.stop="removeProxy">
              <v-icon icon="mdi-delete" />
            </v-btn>
          </v-col>
        </v-row>
      </template>
    </v-expansion-panel-title>
    <v-expansion-panel-text>
      <div class="d-flex flex-row">
        <div class="w-25 mr-2">
          <v-select label="Proxy Type" :items="typeList"/>
        </div>
        <div class="w-25 mr-2">
          <v-text-field v-model="localProxy.name" placeholder="Name" />
        </div>
        <div class="w-25 mr-2">
          <v-chip v-for="(c, index) in localProxy.conditions"
                  :key="index"
                  closable
          >
            {{c.type}}({{c.value}})
          </v-chip>
        </div>
        <div class="d-flex flex-column justify-center ml-auto">
          <div class="d-flex flex-row justify-content-end">
          <v-btn  v-if="!viewConditionMenu"  class="btn-pieker" size="x-small" icon @click.stop="updateConditionMenu">
            <v-icon icon="mdi-plus" />
          </v-btn>
          <form v-else ref="menuWrapper" class="bg-light">
              <div class="menu bg-light shadow rounded d-flex flex-row pl-5">
                <div class="w-100 mr-2">
                  <v-select v-model="currentConditionType" label="Condition" :items="conditionTypeList"
                            @click.stop="stopPropagation"
                            @focus.stop="stopPropagation"
                  />
                </div>
                <div class="w-100 mr-2">
                  <v-text-field v-model="currentConditionValue" placeholder="Number" />
                </div>
                <div class="d-flex flex-column justify-content-center pr-2">
                  <v-btn class="btn-pieker" size="x-small" icon @click.stop="addCondition">
                    <v-icon icon="mdi-check" />
                  </v-btn>
                </div>
              </div>
          </form>
          </div>
        </div>
      </div>

    </v-expansion-panel-text>
  </v-expansion-panel>
</template>

<style scoped lang="scss">
.menu {
  position: relative;
  right:100%;
  z-index: 99;
  width: 200%;
  .v-input{
    height: 100%;
  }
}
</style>
