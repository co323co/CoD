<template>
  <div>
    <v-container class="clothesImgBox" fluid>
      <v-row class="img">
        <v-col cols="12" sm="12" md="12" lg="12">
          <v-card class="clothesImg">
            <v-img height="100%" :src="uploadImg" contain>
              <template v-slot:placeholder>
                <v-row class="fill-height ma-0" align="center" justify="center">
                  <v-icon x-large>mdi-package-variant</v-icon>
                </v-row>
              </template>
            </v-img>
          </v-card>
        </v-col>
      </v-row>
      <v-btn fab style="margin-top: -100px; margin-left: 10px">
        <v-file-input
          class="upload ml-4"
          style="margin-bottom: 9px"
          v-model="file"
          hide-input
          filled
          prepend-icon="mdi-camera"
          @change="change"
        ></v-file-input>
      </v-btn>
    </v-container>
    <v-form>
      <v-container class="inputForm">
        <v-row>
          <v-col cols="12" md="4">
            <h5 v-if="!valid" color="red">필수사항</h5>
            <h5 v-else>필수사항</h5>
          </v-col>

          <v-col class="input" cols="12" md="4">
            <v-select :items="category" label="카테고리" v-model="type" solo dens></v-select>
          </v-col>
          <v-col class="input" cols="12" md="4">
            <v-select :items="colors" label="색상" v-model="color" solo dens></v-select>
          </v-col>
          <v-col class="input" cols="12" md="4">
            <v-select :items="seasons" label="계절" v-model="season" solo dens></v-select>
          </v-col>
          <v-col class="input" cols="12" md="4">
            <v-switch
              inset
              label="공개"
              style="margin-top: -5px; margin-left: 3px"
              color="rgb(133, 125, 177)"
              v-model="isOwned"
            ></v-switch>
          </v-col>
          <v-divider></v-divider>
          <v-col cols="12" md="4"> <h5>선택사항</h5></v-col>
          <v-col class="input" cols="12" md="4">
            <v-text-field label="가격" solo v-model="price"></v-text-field>
          </v-col>
          <v-col class="input" cols="12" md="4">
            <v-text-field label="브랜드" solo v-model="brand"></v-text-field>
          </v-col>
          <v-col class="input" cols="12" md="4">
            <v-text-field label="상품명" solo v-model="name"></v-text-field>
          </v-col>
          <v-col class="input" cols="12" md="4">
            <v-text-field label="사이즈" solo v-model="measure"></v-text-field>
          </v-col>
        </v-row>
      </v-container>

      <!-- 등록 버튼 start -->
      <v-btn icon id="add-click-btn" @click="addClothes()">
        <v-icon class="ml-2" large color="#857DB1" left> mdi-checkbox-marked-circle </v-icon>
      </v-btn>
      <!-- 등록 버튼 end -->
    </v-form>
  </div>
</template>
<script>
import axios from '@/utils/axios';
import { createClothes } from '@/api/clothes';
export default {
  data() {
    return {
      valid: true,
      file: null,
      uploadImg: '',
      category: ['전체', '상의', '하의', '아우터', '신발', '가방', '모자', '악세서리'],
      colors: [
        '빨간색',
        '노란색',
        '주황색',
        '파란색',
        '보라색',
        '회색',
        '흰색',
        '검은색',
        '분홍색',
        '초록색',
      ],
      seasons: ['봄', '여름', '가을', '겨울'],
      name: '',
      type: '',
      isOwned: true,
      season: '',
      measure: '',
      color: '',
      tag: '',
      price: '',
      brand: '',
    };
  },
  methods: {
    // 이미지 업로드
    change(file) {
      console.log(file);
      let ext = file.type.toLowerCase(); //확장자
      // //배열에 추출한 확장자가 존재하는지 체크
      if (
        !(ext == 'image/gif' || ext == 'image/png' || ext == 'image/jpg' || ext == 'image/jpeg')
      ) {
        // resetFormElement($(this)); //폼 초기화
        this.file == null;
        window.alert('이미지 파일이 아닙니다! (gif, png, jpg, jpeg 만 업로드 가능)');
      } else {
        // file = $("#img").prop("files")[0];
        // blobURL = window.URL.createObjectURL(file);
        // $("#image_preview img").attr("src", blobURL);
        // $("#image_preview").slideDown(); //업로드한 이미지 미리보기
        // $(this).slideUp(); //파일 양식 감춤
        let accessToken = this.$store.state.auth.accessToken;
        var fd = new FormData();
        fd.append('images', this.file);
        axios
          .post('/rembg', fd, {
            headers: {
              'X-ACCESS-TOKEN': accessToken,
              'Content-Type': 'multipart/form-data',
            },
          })
          .then((res) => {
            this.uploadImg = res.data;
          })
          .catch((error) => {
            console.log(error.response.data.message);
          });
      }
    },
    addClothes() {
      if (!this.type || !this.color || !this.seasons) {
        this.valid = false;
        console.log(this.valid);
        return;
      }

      let clothes = {
        type: this.type,
        color: this.color,
        season: this.season,
        isOwned: this.isOwned,
        price: this.price,
        brand: this.brand,
        name: this.name,
        measure: this.measure,
        imgUrl: this.uploadImg,
      };

      createClothes(clothes).then(() => {
        this.$router.push({
          name: 'clothesList',
        });
      });
    },
  },
};
</script>
<style scoped>
.clothesImgBox {
  background-color: #d6d6d6;
  padding: 20px;
  /* width: 414px;
  height: 414px; */
}
.clothesImg {
  width: 90vw;
  height: 90vw;
}

.upload {
  align-items: center;
  align-items: center;
  text-align: center;
}
.inputForm {
  padding: 24px;
  margin-bottom: 10px;
}
.input {
  padding-top: 0px;
  padding-bottom: 0px;
}
#add-click-btn {
  position: fixed;
  top: 0.9%;
  right: 4%;
  z-index: 100;
}
</style>
