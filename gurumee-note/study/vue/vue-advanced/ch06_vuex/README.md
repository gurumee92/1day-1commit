Vuex
================

Contents
---------------

1. What is Vuex?
2. Apply Vuex
3. Vuex Map Helper method
4. Store Modularization

What is Vuex?
---------------

`Vuex`란 `Vue`의 상태 관리 라이브러리이다. `Flux` 아키텍처의 변종이며 `React`에서는 `Redux`, `MobX` 등 과 동등한 위치에 있다고 생각하면 된다. 내가 느끼기에는 `Redux`와 형태가 제일 유사하다. `Vuex` 설치는 다음 명령어를 이용한다.

```bash
# install vuex
$ npm i vuex --save
```

Apply Vuex
---------------

`Vuex`를 적용하기 위해서는 다음과 같은 작업이 필요하다.

1. store 구현
    1. state - 애플리케이션 내 관리되는 데이터 집합
    2. actions - 데이터들이 변동될 때 일어나는 작업
    3. mutations - actions 가 일어나면 그 작업의 결과를 state 에게 전달한다.
    4. getters (optional)
2. vue 연결
3. 컴포넌트 연결

이 순서대로 `NewsView`에 `Vuex`를 적용해보자. 먼저 `src` 밑에 `store` 디렉토리를 생성하고 `index.js`를 만든다. 그 후 다음을 입력한다.

vue-news/src/store/index.js
```js
import Vue from 'vue';
import Vuex from 'vuex';

import { fetchNewsList } from '../api/index.js';

Vue.use(Vuex);

export const store = new Vuex.Store({
  state: {
    news: []
  },
  //only call api
  actions: {
    FETCH_NEWS(context) {
        fetchNewsList()
        .then( response => {
            context.commit('SET_NEWS', response.data);
        })
        .catch( error => {
            console.error(error);
        })
    },
  },
  //after call api chages state
  mutations: {
    SET_NEWS(state, data) {
        state.news = data;
    },
  }
})
```

`Redux`처럼, 상태를 일으키는 리듀서 역할을 하는 것들을 대문자로 지정한다. 이제 애플리케이션 내에 `Vuex`를 설치해보자. `main.js`를 다음과 같이 수정한다.

vue-news/src/main.js
```js
import Vue from 'vue';
import App from './App.vue';
import { router } from './routes/index.js';
//vuex load
import { store } from './store/index.js';

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
  router,
  store,
}).$mount('#app')
```

그 후, `NewsView.vue`를 다음과 같이 수정하자.

vue-news/src/views/NewsView.vue
```vue
<template>
    <div>
        <div v-for="item in this.$store.state.news" v-bind:key="item.id">{{ item.title }}</div>
    </div>
</template>

<script>
export default {
    created() {
        this.$store.dispatch('FETCH_NEWS');
    }
}
</script>

<style>

</style>
```

작업 흐름을 살펴보면, 먼저 `NewsView`가 만들어질 때, `store`에게 "FETCH_NEWS"라는 것을 이벤트를 전달한다. 그럼 우리가 정의한 `store`의 actions 프로퍼티에 정의된 "FETCH_NEWS" 메소드가 실행된다. 그러면 내부 "context" 객체에게 "SET_NEWS"와 함께 api에서 불러온 데이터를 같이 전달해준다. 그럼 mutations 의 정의된 "SET_NEWS"가 실행되고 state 의 있는 news 에 전달받은 data 를  할당하는 작업을 한다. 그 작업이 되면 NewsView 에서 그 데이터를 불러와 그려주는 것이다. 즉, 간단히 하면 다음과 같다.

1. `NewsView` created() 호출 시, 그러니까 컴포넌트가 생성될 때, "FETCH_NEWS" 라는 이벤트를 store 에 전달한다.
2. "store"에 정의된 "actions"의 "FETCH_NEWS" 메소드가 호출되어 api에서 데이터를 불러오고 그 데이터를 "context"를 이용하여 "mutations"에 "SET_NEWS"를 전달한다.
3. "store"에 정의된 "mutations"의 "SET_NEWS"에 데이터가 전달되고 이제 "state"에 있는 "news"에다 그 데이터를 전달한다.
4. "store"에 정의된 "state"의 "news"가 데이터를 할당 받으면, "NewsView"가 그것을 받아 렌더링한다.

이제 이렇게 했으면 `AskView`, `JobsView`에도 그 작업을 똑같이 진행해보자. 

vue-news/src/store/index.js
```js
import Vue from 'vue';
import Vuex from 'vuex';

import { fetchNewsList, fetchAskList, fetchJobsList } from '../api/index.js';

Vue.use(Vuex);

export const store = new Vuex.Store({
  state: {
    news: [],
    asks: [],
    jobs: []
  },
  //only call api
  actions: {
    FETCH_NEWS(context) {
        fetchNewsList()
        .then( response => {
            context.commit('SET_NEWS', response.data);
        })
        .catch( error => {
            console.error(error);
        })
    },
    FETCH_ASKS({ commit }) {
        fetchAskList()
        .then( ({ data }) => {
            commit("SET_ASKS", data);
        })
        .catch( error => {
            console.error(error);
        })
    },
    FETCH_JOBS({ commit }) {
        fetchJobsList()
        .then( ({ data }) => {
            commit("SET_JOBS", data);
        })
        .catch( error => {
            console.error(error)
        })
    }
  },
  //after call api chages state
  mutations: {
    SET_NEWS(state, data) {
        state.news = data;
    },
    SET_ASKS(state, data) {
        state.asks = data;
    },
    SET_JOBS(state, data) {
        state.jobs = data;
    }
  }
})
```

vue-news/src/views/AskView.vue
```vue
<template>
    <div>
        <div v-for="item in this.$store.state.asks" v-bind:key="item.id">{{ item.title }}</div>
    </div>
</template>

<script>
export default {
    created() {
        this.$store.dispatch('FETCH_ASKS');
    }
}
</script>

<style>

</style>
```

vue-news/src/views/JobsView.vue
```vue
<template>
    <div>
        <div v-for="item in this.$store.state.jobs" v-bind:key="item.id">{{ item.title }}</div>
    </div>
</template>

<script>
export default {
    created() {
        this.$store.dispatch('FETCH_JOBS');
    }
}
</script>

<style>

</style>
```

Vuex Map Helper method
----------

`NewsView`에서 다음 코드를 보자.


vue-news/src/views/NewsView.vue
```vue
<template>
    <div>
        <div v-for="item in this.$store.state.news" v-bind:key="item.id">{{ item.title }}</div>
    </div>
</template>

<script>
/* ... */
</script>

<style>

</style>
```

`div`태그 아래 `this.$store.state.news`가 보이는가? 실제 지금은 간단한 애플리케이션이라 그렇지 매번 이렇게 불러오면 코드가 지저분해질 것이다. 이것을 깔끔하게 해결할 수 있는 맵핑 헬퍼 메소드들이 존재한다. 먼저 첫번째 방식의 코드이다.

vue-news/src/views/NewsView.vue
```vue
<template>
    <div>
        <div v-for="item in news" v-bind:key="item.id">{{ item.title }}</div>
    </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
    computed: {
        ...mapState({
            news:  state => state.news
        })
    },
    created() {
        this.$store.dispatch('FETCH_NEWS');
    }
}
</script>

<style>

</style>
```

`mapState`를 활용하여 `computed` 속성에 데이터를 넣어주는 방식이다. 이번엔 다른 방식인 `mapGetter`를 사용해보자. 그러려면 `store`에 `getters`를 지정해두어야 한다.

vue-news/src/store/index.js
```js
import Vue from 'vue';
import Vuex from 'vuex';

import { fetchNewsList, fetchAskList, fetchJobsList } from '../api/index.js';

Vue.use(Vuex);

export const store = new Vuex.Store({
  state: { /* 이전과 동일 */ },
  //only call api
  actions: { /* 이전과 동일 */ },
  //after call api chages state
  mutations: { /* 이전과 동일 */ },
  getters: {
      fetchNews(state){
          return state.news
      },
      fetchAsks(state){
          return state.asks
      },
      fetchJobs(state) {
          return state.jobs
      }
  }  
})
```

그 후 `NewsView`를 다음과 같이 수정하자.

vue-news/src/views/NewsView.vue
```vue
<template>
    <div>
        <div v-for="item in news" v-bind:key="item.id">{{ item.title }}</div>
    </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
    computed: {
        ...mapGetters({
            news:  'fetchNews'
        })
    },
    created() {
        this.$store.dispatch('FETCH_NEWS');
    }
}
</script>

<style>

</style>
```

`mapGetter` 방식이 처음에는 복잡할 수 있다. 그러나 나중에 애플리케이션이 커지고 상태 관리가 복잡할수록 아래 방식이 더 편할 것으로 예상된다.

Store Modularization
---------------

이번에는 `store`를 모듈화하는 방법을 알아보겠다. `Vue`에서는 여러 `store`를 만든 후 하나의 `store`로 규합할 수 도 있는데, 현재 강의에서는 각 속성들을 따로 파일로 분리하는 방법을 택했다. `actions, mutations` 속성들만 따로 모듈화 해보자. 먼저 `store` 디렉토리 밑에 `actions.js`, `mutations.js`를 만들고 다음을 입력한다.

vue-news/src/store/actions.js
```js
import { fetchNewsList, fetchAskList, fetchJobsList } from '../api/index.js';

export default {
    FETCH_NEWS(context) {
        fetchNewsList()
        .then( response => {
            context.commit('SET_NEWS', response.data);
        })
        .catch( error => {
            console.error(error);
        })
    },
    FETCH_ASKS({ commit }) {
        fetchAskList()
        .then( ({ data }) => {
            commit("SET_ASKS", data);
        })
        .catch( error => {
            console.error(error);
        })
    },
    FETCH_JOBS({ commit }) {
        fetchJobsList()
        .then( ({ data }) => {
            commit("SET_JOBS", data);
        })
        .catch( error => {
            console.error(error)
        })
    }
};
```

vue-news/src/store/mutations.js
```js
export default {
    SET_NEWS(state, data) {
        state.news = data;
    },
    SET_ASKS(state, data) {
        state.asks = data;
    },
    SET_JOBS(state, data) {
        state.jobs = data;
    }
}
```

그 후, `index.js`를 다음과 같이 수정한다.

vue-news/src/store/index.js
```js
import Vue from 'vue';
import Vuex from 'vuex';

//file load
import actions from './actions.js.js';
import mutations from './mutations.js.js';

Vue.use(Vuex);

export const store = new Vuex.Store({
  state: { /* 이전과 동일 */ },
  //only call api
  actions,
  //after call api chages state
  mutations,
  getters: { /* 이전과 동일 */ }
})
```

이렇게 해서 스토어 모듈화까지 작업했다.