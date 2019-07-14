Async API Call
================

Contents
---------------

1. Make API Module
2. Description JS this
3. Description JS Call & Back
4. Description Promise

Make API Module
---------------

먼저, API 를 호출하여 컴포넌트에 데이터를 뿌려보는 것을 해보자. 첫 번째로 비동기 호출을 위해 프로젝트에서 `axios` 라이브러리를 설치하자. 

```bash
# install axios
$ npm i axios --save
```

`package.json`에서 의존성이 설치된 것 확인했으면, `src` 밑에 `api` 라는 디렉토리를 만들고 `index.js`를 만든 후 다음을 입력하자.

vue-news/src/api/index.js
```js
import axios from "axios";

// HTTP request & response configuration
const config = {
    baseUrl: "https://api.hnpwa.com/v0"
};

// common api function
const fetchNewsList = () => axios.get(`${config.baseUrl}/news/1.json`);
const fetchJobsList = () => axios.get(`${config.baseUrl}/jobs/1.json`);
const fetchAskList = () => axios.get(`${config.baseUrl}/ask/1.json`);

export { 
    fetchNewsList, 
    fetchJobsList, 
    fetchAskList 
};
``` 

`axios` 라이브러리를 이용하여 해당 API를 호출하여 데이터를 가져오는 `fetchXXX` 함수들을 만들고 "export" 구문으로 다른 파일에서 쓸 수 있도록 만들어두었다. 이렇게 하는 큰 이유는 모듈화를 위해서이다. 이제 views 의 각 뷰들에서 이 함수들을 이용해서 필요 데이터를 가져와 뿌리는 작업을 해보자. 먼저 설명을 위해서 `NewsView` 의 코드를 살펴보저. `NewsView`를 다음과 같이 수정하자.

vue-news/src/views/NewsView.vue
```vue
<template>
    <div>
        <div v-for="item in data" v-bind:key="item.id">{{ item.title }}</div>
    </div>
</template>

<script>
// api모듈에서 fetchNewsList 함수를 불러옴.
import { fetchNewsList } from '../api/index.js';

export default {
    // 컴포넌트 내 데이터를 초기화
    data() {
        return {
            data: []
        }
    },
    // 해당 컴포넌트가 만들어질 때 호출되는 메소드, 보통 초기화 작업을 실행함.
    created() {
        fetchNewsList()
             .then( response => this.data = response.data )
             .catch( err => console.error(err) )
    }
}
</script>

<style>

</style>
```

먼저 `script` 부분부터 살펴보자. 주석을 읽으면 손쉽게 알 수 있다. 일단 `Vue` 에서는 컴포넌트마다 `data` 일종의 상태 값을 가질 수 있는데 이들을 만드는 부분이다. 그 후 라이프 사이클 메소드 중 하나인 `created`를 이용하여 뷰 컴포넌트가 생성될 때, api를 호출하여 데이터를 초기화 한다. 보통은 페이지를 랜더링하는 뷰들에게는 이런 작업을 시키지 않지만 빠르게 배우기 위해서 이렇게 하는 것을 알아야 한다. 

이제 `template` 부분을 보면 `div` 태그 아래 `div`를 살펴보자. 실질적으로 api 호출 후 얻은 데이터를 이용하여 뿌려질 것을 그리는 건데 `v-xx` 라는 것을 볼 수 있다. 이것들은 `v-for`는 `Vue` 에서 제공하는 태그 속성 중 하나인데, 간단하게 컴포넌트 내 data 를 읽어서 반복해서 해당 태그를 랜더링 한다고 생각하면 된다. 이때 중요한 것은 `v-bind:key="item.id"` 처럼 해당 태그 속성을 추가해주어야 한다. key 는 말 그대로 겹치지 않는 속성 값을 가져야 한다. 이 작업을 해두지 않으면, 앱 실행시 경고문을 볼 수 있게 된다.

이후는 다 같다. `AskView, JobsView`를 다음과 같이 수정하고 결과를 확인해보자.

vue-news/src/views/AskView.vue
```vue
<template>
    <div>
        <div v-for="item in data" v-bind:key="item.id">{{ item.title }}</div>
    </div>
</template>

<script>
import { fetchAskList } from '../api/index.js';

export default {
    data() {
        return {
            data: []
        }
    },
    created() {
        fetchAskList()
             .then( response => this.data = response.data )
             .catch( err => console.error(err) )
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
        <div v-for="item in data" v-bind:key="item.id">{{ item.title }}</div>
    </div>
</template>

<script>
import { fetchJobsList } from '../api/index.js';

export default {
    data() {
        return {
            data: []
        }
    },
    created() {
        fetchJobsList()
             .then( response => this.data = response.data )
             .catch( err => console.error(err) )
    }
}
</script>

<style>

</style>
```

Description JS this
---------------

JS의 `this`가 가리키는 것은 다음과 같다.

- basic (function, variable) => global(window)
- constructor => instance
- async 
    - function => undefined
    - arrow function => caller instance
    
따라서, ES6 이전에는 비동기 함수 때, 콜백 함수를 작성할 때, this 바인딩이 필수였지만, ES6 이후 `Arrow Function`문법이 나온 이후에는 이러한 바인딩이 필요 없어졌다.

Description JS Call & Back
---------------

콜백이란 함수 호출 시, 함수를 등록하여 그 호출된 함수가 결과를 반환 할 때, 등록한 함수를 호출하는 것을 말한다. 비동기 문법에 이런 구조를 많이 쓰는데 예를 살펴보자.

```js
var result = [];

function fetchData() {
    $.ajax({
        url: "https://example.com/get",
        success: function(data) {
          result = data;
          console.log("#1 ", result)
        }
    })
}

fetchData();
console.log("#2 ", result)
```

이 경우, 출력은 이렇게 된다.

```
#2: result: []
#1: result: [ ... ] <- 데이터가 존재.
```

여기서 `success`에 등록된 함수가 바로 콜백으로 등록된 함수를 뜻한다. (ajax 는 보통 http 연결 후 결과를 반환하기 때문에 #2 보다 늦게 실행이 된다.) 이런 경우 사실 우리의 명령행 구조랑은 전혀 다른 방식으로 동작한다. 일반적인 명령행 구조를 작성하려면 콜백 안에 콜백을 작성하는 콜백 헬 구조를 써야 한다. 이를 해결하기 위한 방식으로 `Promise` 방식이 있다.

 

Description Promise
---------------

JS 에서 콜백 헬 구조를 해결하기 위한 방법으로 `Promise` 구조가 있다. Promise-then-catch 방식으로 비동기 코드를 보다 손쉽고 간결하게 처리할 수 있다. 위의 예제를 `Promise` 구조로 바꿔보자.

```js
var result = [];

function fetchDataUsePromise( ) {
    return new Promise(function(resolve, reject) {
        $.ajax({
            url: '',
            success: function(data) {
                 resolve(data)
            },
            error: function(err) {
                 reject(err)
            }
        })
    })
}

fetchDataUsePromise()
.then( function(response) { 
    result = response.data;
    console.log("#1", result);
})
.catch( function(error) {
    console.error(result)
});

console.log("#2 ", result);
``` 

그러면 이런 출력문을 얻을 수 있다.

```
#1: result: [ ... ] <- 데이터가 존재.
#2: result: [ ... ] <- 데이터가 존재.
```

사실 `Promise` 구조도 콜백헬을 완벽하게 해결할 수는 없다. 그래서 나온 것이 `Await-Async` 문법이다. 이는 다음 강의에 알려준다고 한다.