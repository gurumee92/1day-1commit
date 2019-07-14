Dynamic Route Matching
===================

Contents
---------

1. What is Dynamic Route Matching?
2. Apply Dynamic Route
3. Tag v-html
4. Router Transition

What is Dynamic Route Matching?
---------

`Dynamic Route Matching`이란 동적으로 라우팅을 매칭 시켜주는 것을 말한다. 보통 `users` 를 표현하는 api 가 있다고 가정하면 하나의 `user`를 가져오려면 이런 방식으로 접근한다.

> GET api/users/:id

여기서 `id` 값은 `url`에 따라서 동적으로 매핑된다. 만약 `id`가 숫자형이라고 가정할 때, 1번 유저에게 접근하려면

> GET api/users/1 

2번 유저에게 접근하려면

> GET api/users/2

이런 방식으로 접근하는데 여기서 `id`가 계속 변한다. 물론 하나 하나 매핑할 수 있겠지만, 수 많은 유저들이 존재하는 시스템이라면 이것은 불가능하다. 이 때 필요한 것이 동적 라우팅이다.

Apply Dynamic Route
---------

`Vue.js`에서 동적 라우팅을 적용하려면 `vue-router`가 필요하다. 이미 설치했으니 이는 넘어가자. 이제 `NewsView`, `AskView`의 유저들의 정보를 빼와서, "users/:id" URL 에 동적으로 매핑할 것이다. `NewsView`, `AskView`를 다음과 같이 수정하자.

vue-news/src/views/NewsView.vue
```vue
<template>
    <div>
        <p v-for="item in news" v-bind:key="item.id">
            <div> 
                {{ item.title }}
            </div>
            <small>
                {{item.time_ago}} by 
                <router-link v-bind:to="`/user/${item.user}`">{{item.user}}</router-link>
            </small>
        </p>
    </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
    computed: {
        ...mapState({
            news: state => state.news
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

vue-news/src/views/AskView.vue
```vue
<template>
    <div>
        <p v-for="item in asks" v-bind:key="item.id">
            <router-link :to="`/item/${item.id}`">
                {{ item.title }}
            </router-link>
            <small>
                {{item.time_ago}} by 
                <router-link :to="`/user/${item.user}`">{{item.user}}</router-link>
            </small>
        </p>
    </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
    computed: {
        ...mapGetters({
            asks: 'fetchAsks'
        })
    },
    created() {
        this.$store.dispatch('FETCH_ASKS');
    }
}
</script>

<style>

</style>
```

`AskView`를 보면, `router-link` 부분이 2개이다. 나중에 설명하려 했는데 다 해야겠다. 이 녀석은 api 를 2개 호출하는 것이다. "/item/:id" 에서 Ask 객체 1개를 "/user/:id"에서 User 객체 1개를 가져오는 것이다. 이제 이전과 같이 routes 작업 후에 api 작업, store 작업, 컴포넌트 작업을 순서대로 진행하도록 하겠다. `routes/index.js` 를 다음과 같이 수정하자.

vue-news/src/routes/index.js
```js
import Vue from 'vue';
import VueRouter from 'vue-router';
import NewsView from '../views/NewsView.vue';
import JobsView from '../views/JobsView.vue';
import AskView from '../views/AskView.vue';
import UserView from '../views/UserView.vue';
import ItemView from '../views/ItemView.vue';

Vue.use(VueRouter);

export const router = new VueRouter({
  mode: 'history', //delete #
  routes: [
    /* 이전과 동일 */
    {
      path: '/user/:id',
      component: UserView,
    },
    {
      path: '/item/:id',
      component: ItemView,
    },
  ]
});
```

`/user/:id`, `/item/:id` URL 에 대해서 각각 `UserView`, `ItemView`를 뿌려주도록 동적 라우팅을 해주었다. 이제 store 작업을 하자. `index.js, actions.js, mutations.js`를 차례대로 다음과 같이 수정하자.

vue-news/src/store/index.js
```js
import Vue from 'vue';
import Vuex from 'vuex';

import actions from './actions.js';
import mutations from './mutations.js';

Vue.use(Vuex);

export const store = new Vuex.Store({
  state: {
    /* 이전과 동일 */
    user: {},
    item: {},
  },
  //only call api
  actions,
  //after call api chages state
  mutations,
  getters: {
      /* 이전과 동일 */
      fetchUserInfo(state){
          return state.user;
      },
      fetchItemInfo(state){
          return state.item;
      }
  },
})
```

vue-news/src/store/actions.js
```js
import { fetchNewsList, fetchAskList, fetchJobsList, fetchUserInfo, fetchItemInfo } from '../api/index.js';

export default {
    /* 이전과 동일 */
    FETCH_USER(context, id) {
        fetchUserInfo(id)
        .then( ({ data }) => {
            context.commit("SET_USER", data);
        })
        .catch( error => {
            console.error(error)
        })
    },
    FETCH_ITEM({ commit }, id) {
        fetchItemInfo(id)
        .then( ({ data }) => {
            commit("SET_ITEM", data);
        })
        .catch( error => {
            console.error(error)
        })
    },
};
```

vue-news/src/store/mutations.js
```js
export default {
    /* 이전과 동일 */
    SET_USER(state, data) {
        state.user = data;
    },
    SET_ITEM(state, data) {
        state.item = data;
    }
}
```

자, 마지막으로 View 작업을 해보자. `UserView.vue, ItemView.vue`를 차례대로 다음과 같이 수정하자.

vue-news/src/views/UserView.vue
```vue
<template>  
    <div>
        <p>{{ userInfo.id }}</p>
        <p>{{ userInfo.karma }}</p>
        <p>{{ userInfo.created }}</p>
    </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
    computed: {
        ...mapGetters({
            userInfo: 'fetchUserInfo' 
        })
    },

    created() {
        const id = this.$route.params.id;
        this.$store.dispatch("FETCH_USER", id);
    }
}
</script>

<style>

</style>
```

vue-news/src/views/ItemView.vue
```vue
<template>
    <div>
        <!-- details -->
        <section>
            <div class="user-container">
                <div>
                    <i class="fas fa-user"></i>
                </div>
                <div class="user-description">
                    <router-link :to="`/user/${item.user}`">{{ item.user }}</router-link> 
                    <div class="time">
                        {{ item.time_ago }}
                    </div>
                </div>
            </div>
            <h2>{{ item.title }}</h2>
        </section>
        <!-- comments -->
        <section>
            {{ item.content }}
        </section>
    </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
    computed: {
        ...mapGetters({
            item: 'fetchItemInfo'
        })
    },    
    created() {
        const id = this.$route.params.id;
        this.$store.dispatch("FETCH_ITEM", id);
    }
}
</script>

<style scoped>

.user-container{
    display: flex;
    align-items: center;
    padding: 0.5rem;
}

.fa-user {
    font-size: 2.5rem;
}

.user-description{
    padding-left: 8px;
    flex: 1;
}

.time {
    font-size: 0.7rem;
}
</style>
```

여기까지 했으면, 모든게 정상 동작하는 것을 볼 수 있다.

Tag v-html 
---------

그런데, 이상하다. `ItemView`를 뿌려줄 때 HTML 태그들이 그대로 보여진다. 이를 어떻게 해결할 수 있을까. 다른 곳은 모르겠지만 `Vue.js`에서는 굉장히 간단하게 처리할 수 있다. 먼저 이러한 현상이 왜 일어나냐면 HTML 태그들이 일반 텍스트로 취급되기 때문이다. 따라서 이것을 HTML 태그로 인식하게 하면 된다. `Vue.js`에서는 `v-html` 디렉티브라는 녀석으로 이 작업을 손쉽게 처리한다. "ItemView.vue"를 다음과 같이 수정하자.

vue-news/src/views/ItemView.vue
```vue
<template>
    <div>
        <!-- details -->
        <section>
            <div class="user-container">
                <div>
                    <i class="fas fa-user"></i>
                </div>
                <div class="user-description">
                    <router-link :to="`/user/${item.user}`">{{ item.user }}</router-link> 
                    <div class="time">
                        {{ item.time_ago }}
                    </div>
                </div>
            </div>
            <h2>{{ item.title }}</h2>
        </section>
        <!-- comments -->
        <section>
            <!-- 수정 부분 -->
            <div v-html=item.content />
        </section>
    </div>
</template>

<script>
/* 이전과 동일 */
</script>

<style scoped>
/* 이전과 동일 */
</style>
```

이러면 HTML 태그들이 더 이상 보이지 않고 문서로 잘 보이는 것을 확인할 수 있다. 

Router Transition
---------

마지막으로 한 가지만 더 보자. 더 나은 UX 를 위해서 라우팅 될 때 조금 더 부드럽게 화면이 동작하도록 만들어보자. 이를 트랜지션이라고 하는데 일종의 애니메이션을 넣는다고 생각하면 된다. 이 작업은 생각보다 간단한다. `App.vue`를 다음과 같이 수정하자.

vue-news/src/App.vue
```vue
<template>
  <div id="app">
    <tool-bar></tool-bar>
    <transition name="page">
      <router-view></router-view>
    </transition>
  </div>
</template>

<script>
import ToolBar from './components/ToolBar.vue';

export default {
  components: {
    ToolBar,
  }
}
</script>

<style>
body {
  padding: 0;
  margin: 0;
}

.page-enter-active, .page-leave-active {
  transition: opacity .5s;
}
.page-enter, .page-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
}
</style>
```

바뀐것은 style 부분과 `router-view` 태그를 `transition` 태그로 감싸준 밖에 없다. 그러나, 이 작업만 하더라도 화면이 URL에 따라 바뀔 때마다 스무스하게 바뀌는 것을 확인할 수 있다.