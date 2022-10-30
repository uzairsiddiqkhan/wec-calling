let localVideo = document.getElementById("local-video")
let remoteVideo =document.getElementById("remote-video")
let localStream
let remoteStream
let peer


localVideo.style.opacity =0
remoteVideo.style.opacity =0

localVideo.onplaying = ()=>{
    localVideo.style.opacity=1
}

remoteVideo.onplaying = ()=>{
    remoteVideo.style.opacity=1
}


function init (userId){
    peer =new Peer(userId,
        {
        host : "192.168.0.117",
        port : 9000,
        path : '/androidweb_RTC'
    })

   
    peer.on('open',()=>{
    // we will make a call to android function
    Android.onPeerConnected()
     })

     listen()


}

function listen(){
    peer.on( "call" ,(call) => {

        navigator.getUserMedia({
            audio : true ,
            video  :true
        }, (stream)=>{

           localVideo.srcObject = stream
           localStream =stream
           
           call.answer(stream)

           call.on('stream',(remoteStream)=>{

            remoteVideo.srcObject =remoteStream
            remoteStream = remoteStream
          

            remoteVideo.className = "primaryVideo"
            localVideo.className ="secondaryVideo"

            
           })


        }
        )
    })
}

function startCall(otherUserId){
    navigator.getUserMedia({
        audio :true ,
        video :true 
        
    }, (stream)=>{
        localVideo.srcObject = stream
        localStream = stream


        const call = peer.call(otherUserId,stream)

        call.on('stream', (remoteStream)=>{

            remoteVideo.srcObject =remoteStream
           remoteStream = remoteStream
            
            

            remoteVideo.className = "primaryVideo"
            localVideo.className =  "secondaryVideo"

           

        })


    })
}

function toggleVideo(b){
    if(b == "true" ){
        localStream.getVideoTracks()[0].enabled =true
    }
    else{
        localStream.getVideoTracks()[0].enabled =false
    }
}


function toggleAudio(b){
    if(b == "true" ){
        localStream.getAudioTracks()[0].enabled =true
    }
    else{
        localStream.getAudioTracks()[0].enabled =false
    }
}