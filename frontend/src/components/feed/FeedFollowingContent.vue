<template>
  <v-container style="padding: 0 0 70px 0">
    <div v-if="codiList.length == 0" class="mt-16 pt-10 text-center">
      <v-icon x-large class="mt-16 mb-3">mdi-package-variant</v-icon>
      <h4>아직 아무도 코디를 올리지 않았어요 😥</h4>
    </div>
    <div v-for="codi in codiList" :key="codi.codiId">
      <v-flex style="padding: 12px 12px 8px 12px">
        <div class="profileInfo">
          <v-row>
            <v-col cols="auto" style="padding-right: 0">
              <v-img
                v-if="!codi.userProfileImg"
                @click="userClick(codi.userId)"
                class="profileImg"
                src="../../assets/logo/login.png"
              />
              <v-img
                v-else
                @click="userClick(codi.userId)"
                class="profileImg"
                :src="codi.userProfileImg"
              />
            </v-col>
            <v-col cols="auto" style="padding: 20px 0px 18px 12px">
              <h4 @click="userClick(codi.userId)">{{ codi.userNickname }}</h4>
            </v-col>
          </v-row>
        </div>
      </v-flex>
      <v-flex>
        <div>
          <v-img :src="codi.codiThumbnail" style="background-color: rgb(133 125 177 / 35%)" />
        </div>
      </v-flex>
      <v-flex style="padding: 12px">
        <v-row>
          <!-- 좋아요 -->
          <v-col cols="auto" sm="12" md="12" lg="12" style="padding-right: 0"
            ><v-icon
              v-if="isLiked(codi.codiId)"
              @click="deleteCodiLiked(codi.codiId)"
              color="#CCBEE3"
              >mdi-heart</v-icon
            >
            <v-icon
              v-if="!isLiked(codi.codiId)"
              @click="createCodiLiked(codi.codiId)"
              color="#CCBEE3"
              >mdi-heart-outline</v-icon
            >
            <h5 class="purpleText">{{ codi.liked }}</h5>
          </v-col>
          <!-- 댓글 -->
          <v-col cols="auto" sm="12" md="12" lg="12">
            <v-icon @click="comment(codi.codiId)" color="#CCBEE3">mdi-comment</v-icon>
            <h5 class="purpleText">{{ codi.comment }}</h5>
          </v-col>
          <!-- 공백 -->
          <v-spacer></v-spacer>
          <!-- 등록일 -->
          <v-col class="createAt" cols="7" sm="12" md="12" lg="12" align="right">
            <h5 class="grayText"></h5
          ></v-col>
        </v-row>
      </v-flex>
      <v-flex style="padding: 0 12px 0 12px">
        <v-row>
          <v-col cols="auto" style="padding-bottom: 0">
            <h5 style="font-weight: bold">{{ codi.userNickname }}</h5>
          </v-col>
          <v-col style="padding-left: 0; padding-bottom: 0">
            <h5>{{ codi.codiDescription }}</h5>
          </v-col>
        </v-row>
        <v-row>
          <v-col
            cols="auto"
            v-for="(tag, index) in splitTag(codi.codiTag)"
            :key="tag"
            class="mt-1 mb-1"
            style="padding: 0 0 12px 5px"
          >
            <h5 v-if="index >= 1" class="tag">#{{ tag }}</h5>
            <h5 v-if="index < 1" style="padding-left: 7px" class="tag">#{{ tag }}</h5>
          </v-col>
        </v-row>
      </v-flex>
      <!-- <feed-comment /> -->
      <!-- <feed-comment v-bind:codi="codi" /> -->
    </div>
  </v-container>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import { createCodiLiked, deleteCodiLiked } from '@/api/codiLiked';
export default {
  props: ['codi'],
  data() {
    return {
      codiList: [],
      likedList: [],
      // like: "false",
    };
  },
  computed: {
    ...mapGetters(['followingCodies', 'codiLikedList']),
  },
  watch: {
    followingCodies: function () {
      console.log('팔로탭 코디 watch : ', this.followingCodies);
      this.codiList = this.followingCodies;
    },
    codiLikedList: function () {
      this.likedList = this.codiLikedList;
      this.setFollowingCodies();
      this.setPopularCodies();
    },
  },

  created() {
    this.setFollowingCodies();
    this.setCodiLikedList();
  },
  methods: {
    ...mapActions(['getFollowingCodies', 'getCodiLiked', 'getPopularCodies']),
    setFollowingCodies() {
      let accessToken = this.$store.state.auth.accessToken;
      let payload = { page: 1, size: 10, accessToken: accessToken };
      this.getFollowingCodies(payload);
      this.codiList = this.followingCodies;
    },
    setPopularCodies() {
      let payload = { startDate: '2021-10-23', endDate: '2021-11-15', page: 1, size: 10 };
      this.getPopularCodies(payload);
    },
    splitTag(text) {
      return text.split(',');
    },
    isLiked(codiId) {
      // console.log(this.$store.state.feed.codiLikedList.codiId);
      for (var i = 0; i < this.$store.state.feed.codiLikedList.length; i++) {
        // console.log("좋아요 리스트: " + this.$store.state.feed.codiLikedList[i].codiId);
        if (this.$store.state.feed.codiLikedList[i].codiId === codiId) {
          // console.log("있음" + this.$store.state.feed.codiLikedList[i].codiId);
          // this.like=true;
          return true;
        }
      }
      return false;
    },
    setCodiLikedList() {
      let accessToken = this.$store.state.auth.accessToken;
      let payload = { page: 1, size: 10, accessToken: accessToken };
      this.getCodiLiked(payload);
      this.likedList = this.codiLikedList.map((a) => a.codiId);
    },
    createCodiLiked(codiId) {
      let accessToken = this.$store.state.auth.accessToken;
      let payload = { codiId: codiId };
      createCodiLiked(payload, accessToken).then(() => {
        this.setCodiLikedList();
        this.setFollowingCodies();
        console.log('좋아요 성공');
      });
    },
    deleteCodiLiked(codiId) {
      let accessToken = this.$store.state.auth.accessToken;
      deleteCodiLiked(codiId, accessToken).then(() => {
        this.setCodiLikedList();
        this.setFollowingCodies();
        console.log('좋아요 취소');
      });
      this.setCodiLikedList();
    },
    comment(codiId) {
      this.$router.push({
        path: 'comment/' + codiId,
      });
    },
    userClick(userId) {
      this.$router.push({
        path: '../mypage/' + userId,
      });
    },
  },
};
</script>

<style scoped>
.profileImg {
  border-style: solid;
  border-color: #857db1;
  border-radius: 50px;
  max-height: 45px;
  max-width: 45px;
}
.profileInfo {
  display: inline-block;
}
.col {
  vertical-align: middle;
}

.createAt {
  margin: auto;
}

.grayText {
  color: #a9a9a9;
  display: inline;
}
.purpleText {
  color: #c4bddd;
  display: inline;
  padding-left: 5px;
  /* font-family: BMHANNAAir !important; */
}
.tag {
  color: #c4bddd;
  /* display: inline; */
  /* padding-right: 5px; */
}
</style>
