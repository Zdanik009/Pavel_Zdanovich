function example_control(player_id) {
    let start_time = 0;
    let duration = 10;
    let counter = 0;
    let player = document.getElementById(player_id);
    player.currentTime = start_time;
    player.addEventListener("timeupdate", function () {
        if (player.currentTime < start_time || player.currentTime > start_time + duration) {
            player.pause();
            player.currentTime = start_time;
        }
    });
    player.addEventListener("pause", function () {
        counter++;
        if (counter === 2) {
            player.muted = true;
            duration = 0;
        }
        if (counter === 3) {
            player.muted = false;
            duration = 10000;
            counter = 0;
        }
    });
    player.addEventListener("playing", function () {
        if (counter === 2) {
            player.muted = true;
            player.pause();
        }
    });
}