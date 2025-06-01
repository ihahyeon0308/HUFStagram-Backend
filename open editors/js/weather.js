const API_KEY = "859e38d1118d4d945a29341186db0b4f";

function onGeoOk(position) {
  const lat = position.coords.latitude;
  const lon = position.coords.longitude;
  const url = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${API_KEY}&units=metric`;

  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      const weather = document.querySelector("#weather-text");
const city = document.querySelector("#city-text");
      city.innerText = data.name;
      weather.innerText = `${data.weather[0].main} / ${data.main.temp}°C`;
    });
}

function onGeoError() {
  alert("위치를 찾을 수 없습니다. 위치 접근을 허용해주세요.");
}

navigator.geolocation.getCurrentPosition(onGeoOk, onGeoError);

// function onGeoOk(position) {
//   const lat = position.coords.latitude;
//   const lng = position.coords.longitude;
//   console.log("You live in", lat, lng);
// }
// function onGeoError() {
//   alert("Can't find you. No weather for you.");
// }
// // You live in 37.6045568 126.6581504
// navigator.geolocation.getCurrentPosition(onGeoOk, onGeoError);