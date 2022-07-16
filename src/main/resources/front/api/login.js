function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function sendMsgApi(phone){
    return $axios({
        'url': 'user/code',
        'method': 'post',
        data: phone
    })
}

function loginoutApi() {
  return $axios({
    'url': '/user/logout',
    'method': 'post',
  })
}

  