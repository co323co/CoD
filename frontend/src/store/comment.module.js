/*

참고하라고 둔 코드

*/

import { getCommentList } from "@/api/comment";

export default {
  // 현재 상태들
  state: {
    //현재 목록에 있는 코멘트들
    comments: [],
  },
  getters: {
    comments(state) {
      return state.comments;
    },
  },
  mutations: {
    setComments(state, payload) {
      state.comments = payload;
    },
  },
  actions: {
    //현재 띄워줄 댓글 리스트 가져오기
    getComments: ({ commit }, payload) => {
      getCommentList(payload)
        .then((res) => {
          commit("setComments", res);
        })
        .catch(function (err) {
          console.log(err);
        });
    },
  },
};
