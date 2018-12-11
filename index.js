(function(){
  //Initialize firebase
  const config = {
    apiKey: "AIzaSyAqn7xgAz9DcOQaP2UI_X003piFAdlT9qc",
    authDomain: "final-project-41029.firebaseapp.com",
    databaseURL: "https://final-project-41029.firebaseio.com",
    projectId: "final-project-41029",
    storageBucket: "final-project-41029.appspot.com",
    messagingSenderId: "604417706203"
  };
  firebase.initializeApp(config);

  //Get elements
  const txtEmail = document.getElementById('txtEmail');
  const txtPassword = document.getElementById('txtPassword');
  const btnLogin = document.getElementById('btnLogin');
  const btnSignUp = document.getElementById('btnSignUp');
  const btnLogout = document.getElementById('btnLogout');

  //Add login environment
  btnLogin.addEventListener('click',e => {
    //Get email and password
    const email = txtEmail.value;
    const pass = txtPassword.value;
    const auth = firebase.auth();

    //Sign in
    const promise = auth.signInwithEmailAndPassword(email,pass);
    promise.catch(e=>console.log(e.message));
  });
})
