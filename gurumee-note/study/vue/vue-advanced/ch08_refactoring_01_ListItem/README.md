Refactoring #1 ListItem
========================

Contents
--------------

1. Refactoring

Refactoring
--------------

이번엔 리팩토링을 할 것이다. 요즘은 프론트엔드든 백엔드든 컴포넌트 지향 개발을 하는 것이 트랜드이다. 제일 먼저 리팩토링해서 공통화해서 뺄 컴포넌트는 `ListItem` 이다. `NewsView, AskView, JobsView` 들은 각각 뿌려줄 아이템 목록이 존재한다. 그러나 몇몇 속성만 다를 뿐 이들의 형태는 유사하다. 이것을 하나의 `ListItem`이라는 컴포넌트로 분리하겠다. 다음은 `ListItem.vue` 코드이다. 그 전에 작업을 조금 더 쉽게 하기 위해서 `routes/index.js`를 다음과 같이 수정하자.

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
    {
      path: '/',
      redirect: '/news',
    },
    {
      // url
      path: '/news',
      name: 'news',
      // rendering component
      component: NewsView,
    },
    {
      path: '/ask',
      name: 'ask',
      component: AskView,
    },
    {
      path: '/jobs',
      name: 'jobs',
      component: JobsView,
    },
    /* 이전과 동일 */
  ]
});
```

바로 라우트의 이름으로 바로 접근할 수 있게끔 프로퍼티를 만들어준 것이다. 이제 컴포넌트가 불러온 라우트의 이름을 알고 싶다면 `this.$route.name` 으로 접근할 수 있다. 이제 `components` 폴더 밑에 `ListItem.vue`를 만들고 다음과 같이 코드를 작업한다.

vue-news/src/components/ListItem.vue
```vue
<template>
    <ul class="item-list">
        <li v-for="item in items" v-bind:key="item.id" class="item">
            <!-- point -->
            <div class="item-points">
                {{ item.points || 0 }}
            </div>
            <!-- etc -->
            <div class="etc">
                <p class="item-title">
                    <template v-if="item.domain">
                        <a :href="item.url">{{ item.title }}</a>
                    </template>
                    <template v-else>
                        <router-link :to="`/item/${item.id}`">{{ item.title }}</router-link>
                    </template>
                </p>
                <small class="link-text">
                    {{ item.time_ago }} by 
                    <router-link v-if="item.user"
                                    v-bind:to="`/user/${item.user}`">
                        {{ item.user }}
                    </router-link>
                    <a :href="item.url" v-else>
                        {{ item.domain }}
                    </a>   
                </small>
            </div>
        </li>
    </ul>
</template>

<script>
export default {
    computed: {
        items() {
            const name = this.$route.name;

            if (name === "news") {
                return this.$store.state.news;
            } else if (name === "ask") {
                return this.$store.state.asks;
            } else if (name === 'jobs') {
                return this.$store.state.jobs;
            }
        }
    },
    created() {
        const name = this.$route.name;

        if (name === "news") {
            this.$store.dispatch('FETCH_NEWS');
        } else if (name === "ask") {
            this.$store.dispatch('FETCH_ASKS');
        } else if (name === 'jobs') {
            this.$store.dispatch('FETCH_JOBS');
        }
    }
}
</script>

<style scoped>
.item-list {
    margin: 0;
    padding: 0;
}

.item {
    list-style: none;
    display: flex;
    align-items: center;
    border-bottom: 1px solid wheat;
}

.item-points {
    width: 80px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #41b883;
}

.item-title {
    margin: 0;
}

.link-text {
    color: lightgray;
}
</style>
```

먼저, script 부분을 보면 route 이름 별로 데이터를 스토어에서 FETCH 하는 것을 알 수 있다. 또한, 각 뷰마다 조금씩 아이템의 형태가 다른데, 예를 들면 news, jobs 만 domain 필드가 있고 news, ask 만 user 필드가 있다. 이런 세세한 컨트롤은 `v-if` 디렉티브를 이용한다. 대표적으로 2가지 방식이 있는데, 위 코드에 다 나와 있다.

* template 방식
    ```vue
    <p class="item-title">
        <template v-if="item.domain">
            <a :href="item.url">{{ item.title }}</a>
        </template>
        <template v-else>
            <router-link :to="`/item/${item.id}`">{{ item.title }}</router-link>
        </template>
    </p>
    ```
* tag 방식
    ```vue
    <small class="link-text">
        {{ item.time_ago }} by 
        <router-link v-if="item.user"
                        v-bind:to="`/user/${item.user}`">
            {{ item.user }}
        </router-link>
        <a :href="item.url" v-else>
            {{ item.domain }}
        </a>   
    </small>
    ```

이렇게 컴포넌트로 따로 분리했으면 `NewsView, AskView, JobsView`를 다음과 같이 수정하자.

vue-news/src/views/NewsView.vue
```vue
<template>
    <list-item></list-item>
</template>

<script>
import ListItem from '../components/ListItem.vue';

export default {
    components: {
        ListItem,
    }
}
</script>

<style scoped>

</style>
```

vue-news/src/views/AskView.vue
```vue
<template>
    <list-item></list-item>
</template>

<script>
import ListItem from '../components/ListItem.vue';

export default {
    components: {
        ListItem,
    }
}
</script>

<style scoped>

</style>
```

vue-news/src/views/JobsView.vue
```vue
<template>
    <list-item></list-item>
</template>

<script>
import ListItem from '../components/ListItem.vue';

export default {
    components: {
        ListItem,
    }
}
</script>

<style scoped>

</style>
```

신기하게도 코드가 완벽하게 똑같이 된다. 이것들을 분리할 수 있으면 참 좋을 것 같다.