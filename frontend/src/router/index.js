import Vue from 'vue';
import Router from 'vue-router';
import store from '@/store';

Vue.use(Router);

const router = new Router({
  routes: [
    {
      path: '/',
      redirect: '/main',
      component: () => import('@/layouts/MainLayout'),
      children: [
        {
          path: '/main',
          name: 'main',
          component: () => import('@/pages/Main/Main.vue'),
        },
        {
          path: '/record-codi/select',
          name: 'record-codi-select',
          component: () => import('@/pages/Main/RecordCodi/SelectCodi.vue'),
        },
        {
          path: '/record-codi/coordination',
          name: 'coordination',
          component: () => import('@/pages/Main/RecordCodi/Coordination.vue'),
        },
        {
          path: '/record-codi/regist',
          name: 'record-codi-regist',
          component: () => import('@/pages/Main/RecordCodi/RegistCodi.vue'),
        },
        {
          path: '/record-codi/update/:id',
          name: 'record-codi-update',
          component: () => import('@/pages/Main/RecordCodi/RegistCodi.vue'),
        },
        {
          path: '/record-codi/detail',
          name: 'record-codi-detail',
          component: () => import('@/pages/Main/RecordCodi/CalendarDetail.vue'),
        },
      ],
    },
    {
      path: '/sign-in',
      name: 'sign-in',
      component: () => import('@/pages/sign/SignIn.vue'),
    },
    {
      path: '/sign-up',
      name: 'sign-up',
      component: () => import('@/pages/sign/SignUp.vue'),
    },
    {
      path: '/codi',
      name: 'codi',
      redirect: '/codi/list',
      component: () => import('@/layouts/CodiLayout'),
      children: [
        {
          name: 'codiCreate',
          path: 'create',
          component: () => import('@/pages/codi/CodiCreate.vue'),
        },
        {
          name: 'codiList',
          path: 'list',
          component: () => import('@/pages/codi/CodiList.vue'),
        },
        {
          name: 'codiDetail',
          path: 'detail/:no',
          component: () => import('@/pages/codi/CodiDetail.vue'),
        },
      ],
    },
    {
      path: '/mypage',
      name: 'mypage',
      redirect: '/mypage/:no',
      component: () => import('@/layouts/MypageLayout'),
      children: [
        {
          name: 'mypageMain',
          path: ':no',
          component: () => import('@/pages/mypage/MypageMain.vue'),
        },
        {
          name: 'mypageFollow',
          path: ':no/follow',
          component: () => import('@/pages/mypage/MypageFollow.vue'),
        },
      ],
    },
    {
      path: '/feed',
      name: 'feed',
      redirect: '/feed/main',
      component: () => import('@/layouts/FeedLayout.vue'),
      children: [
        {
          path: 'main',
          name: 'feedMain',
          component: () => import('@/pages/feed/FeedMain.vue'),
        },
        {
          path: 'search',
          name: 'feedSearch',
          component: () => import('@/pages/feed/FeedSearch.vue'),
        },
        {
          path: 'comment/:no',
          name: 'feedComment',
          component: () => import('@/pages/feed/FeedComment.vue'),
        },
      ],
    },
    {
      path: '/clothes',
      name: 'clothes',
      redirect: '/clothes/list',
      component: () => import('@/layouts/ClothesLayout'),
      children: [
        {
          name: 'clothesCreate',
          path: 'create',
          component: () => import('@/pages/clothes/ClothesCreate.vue'),
        },
        {
          name: 'clothesList',
          path: 'list',
          component: () => import('@/pages/clothes/ClothesList.vue'),
        },
        {
          name: 'clothesDetail',
          path: 'detail/:no',
          component: () => import('@/pages/clothes/ClothesDetail.vue'),
        },
      ],
    },
    {
      path: '/setting',
      name: 'setting',
      redirect: '/setting',
      component: () => import('@/layouts/SettingLayout'),
      children: [
        {
          name: 'settingMain',
          path: '',
          component: () => import('@/pages/setting/settingMain.vue'),
        },
        {
          name: 'settingLikedCodi',
          path: 'likedCodi',
          component: () => import('@/pages/setting/settingLikedCodi.vue'),
        },
        {
          name: 'settingUpadateProfile',
          path: 'upadateProfile',
          component: () => import('@/pages/setting/settingUpadateProfile.vue'),
        },
        {
          name: 'settingInfo',
          path: 'info',
          component: () => import('@/pages/setting/settingInfo.vue'),
        },
        {
          name: 'settingChangePassword',
          path: 'changePassword',
          component: () => import('@/pages/setting/settingChangePassword.vue'),
        },
      ],
    },
    {
      path: '/:catchAll(.*)*',
      component: () => import('../pages/Error404.vue'),
    },
  ],

  // #??? ???????????? ?????? history ??? ????????? ????????????.
  mode: 'history',
});

export default router;

router.beforeEach((to, from, next) => {
  let loginUser = store.getters.loginUser;
  console.log('loginUser', loginUser);
  if (loginUser || to.name == 'sign-in' || to.name == 'sign-up') {
    next();
  } else {
    // alert('???????????? ????????? ??????????????????.');
    next('/sign-in');
  }
});
