<script setup lang="ts">
import { toRef, watch } from 'vue'
import ProxyEdit from '@/components/ProxyEdit.vue'

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue', 'close-step-edit'])

const localStep = toRef(props, 'modelValue')

const closeStepEdit = () => {
  emit('close-step-edit')
}

const addProxy = () => {
  localStep.value.proxies.push({
    name: '',
    type: '',
    conditions: []
  })
}

const addUserTraffic = () => {
  localStep.value.userTraffics.push({
    name: '',
    type: '',
    target: '',
    conditions: []
  })
}

const updateProxy = (index:number, updated) => {
  localStep.value.proxies[index] = updated
}

const removeProxy = (index: number) => {
  localStep.value.proxies.splice(index, 1)
}

// Watch for local changes and emit back to parent
watch(
  localStep,
  (newVal) => {
    emit('update:modelValue', newVal)
  },
  { deep: true },
)
</script>

<template>
  <div class="view-blocker p-4">
    <div class="d-flex flex-column bg-light w-100 overflow-auto">
      <section class="header-section d-flex flex-row p-3">
        <div class="d-flex flex-row">
          <h1>Test-Step Editor:</h1>
        </div>
        <div class="d-flex flex-row ml-2">
          <h1>{{localStep.name || 'Undefined Name'}}</h1>
        </div>
        <div class="ml-auto">
          <v-btn @click="closeStepEdit">
            <v-icon icon="mdi-content-save"></v-icon>
          </v-btn>
          <v-btn color="error" @click="closeStepEdit">
            <v-icon icon="mdi-close"></v-icon>
          </v-btn>
        </div>
      </section>
      <section class="body-section d-flex flex-column w-100 p-5">
        <div class="d-flex flex-row">
          <div class="mb-3 w-25 mr-auto">
            <v-text-field v-model="localStep.name" placeholder="Step Name" />
          </div>
          <div class="mb-3 w-50">
            <v-textarea v-model="localStep.doc" rows="1" placeholder="Step Docs" />
          </div>
        </div>
        <div class="d-flex flex-column border-top">
          <div class="d-flex flex-row justify-content-evenly mt-4">
            <v-btn class="btn-pieker" @click="addProxy">
              <v-icon icon="mdi-air-filter"/>
              Add Proxy
            </v-btn>
            <v-btn class="btn-pieker" @click="addUserTraffic">
              <v-icon icon="mdi-account-voice"/>
              Add User-Traffic
            </v-btn>
          </div>
          <div class="d-flex flex-column mt-4">
            <v-expansion-panels>
              <ProxyEdit v-for="(proxy,index) in localStep.proxies" :key="index"
                         :modelValue="proxy"
                         @update:modelValue="updateProxy(index, $event)"
                         @removeProxy="removeProxy"
                />
            </v-expansion-panels>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '../assets/colors';

.view-blocker {
  position: fixed;
  top: 3rem;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 99;
}

.header-section {
  background-color: colors.$primary-second;
  color: colors.$secondary-second;

  .v-btn {
    margin-left: 1rem;
  }

  h1 {
    font-size: 1.5rem !important;
  }
}

.body-section{
  height: 80vh;
}

</style>
