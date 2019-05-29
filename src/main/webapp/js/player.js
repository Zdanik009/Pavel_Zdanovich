audioElements = document.getElementsByTagName('audio');
playing = true;

audioElements.forEach(audio => {
    audio.addEventListener('play', function () {
        if (playing) {
            audioElements.forEach(audio => {
                audio.pause();
            });
        }
        if (this.paused) {
            playing = false;
            this.play();
        } else {
            playing = true;
        }
    });
});

function control(player_id, tour_status, start_time, enable) {
    if (tour_status !== 'submitted') {
        let counter = 2;
        let player = document.getElementById(player_id);
        player.currentTime = start_time;
        player.addEventListener("timeupdate", function () {
            if (player.currentTime < start_time || player.currentTime > start_time + 10) {
                player.pause();
                player.currentTime = start_time;
            }
        });
        player.addEventListener("pause", function () {
            counter--;
            if (counter <= 0) {
                enable = false;
                player.muted = true;
            }
            if (counter <= -3) {
                alert("YOU CAN NO LONGER LISTEN TO THIS MUSIC CLIP!");
            }
        });
        player.addEventListener("playing", function () {
            if (!enable) {
                player.muted = true;
                player.pause();
            }
        });
    }
}


