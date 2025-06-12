<script setup lang="ts">
const menu = [
  {text: 'Home', href: '/'},
  /*{text: 'Editor', href: '/editor'},*/
  {text: 'Tests', href: '/tests'}
]

const links = [
  {text: 'GitHub', href: 'https://github.com/PIEKER/Pieker'},
  {text: 'Contact Us', href: ''}
]
</script>

<template>
  <header>
    <v-card>
      <v-tabs class="nav" align-tabs="start">
        <div class="logo-container">
          <a href="/">
            <img alt="Pieker logo" class="logo" src="./assets/img/pieker-logo-minimal.svg" width="30" height="30" />
          </a>
        </div>
        <v-tab v-for="menuItem in menu" :key="menuItem.text" :to="menuItem.href">{{menuItem.text}}</v-tab>
      </v-tabs>
    </v-card>
  </header>
  <div class="content-wrapper">
    <div class="warning bg-warning p-2 position-fixed">
      Most pages are under construction and not fully developed yet. Its purpose is a proof of concept of a web application.
    </div>
    <router-view></router-view>
  </div>
  <v-footer class="d-flex align-center justify-center ga-2 flex-wrap flex-grow-1 py-3" color="surface-light">
    <a v-for="link in links" :href="link.href" :key="link.text">
      <v-btn
        :text="link.text"
        variant="text"
        rounded
        :href="link.href"/>
    </a>

    <div class="flex-1-0-100 text-center mt-2">
      {{ new Date().getFullYear() }} — <strong>PIEKER</strong>
    </div>
  </v-footer>
</template>

<style scoped lang="scss">
@use '@/assets/colors.scss';

header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  line-height: 1.5;
  z-index: 100;
  .v-card{
    border-radius: 0
  }
}
.warning{
  top: 50px;
  width: 100%;
  z-index: 100;
}

footer{
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
}

.nav {
  background-color: colors.$primary-third;
  color: colors.$secondary-second;
  width: 100%;
  font-size: 15px;
  text-align: center;
}

.logo-container{
  display: flex;
  flex-flow: column nowrap;
  justify-content: center;
  width: 10%;
}
a {
  color: inherit;
  text-decoration: none;
}
.content-wrapper{
  margin-top: 50px;
}

</style>
