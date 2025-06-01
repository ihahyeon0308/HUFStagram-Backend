const images = ["0.jpg", "1.jpg", "2.jpg"];
const chosenImage = images[Math.floor(Math.random() * images.length)];

const bgImage = document.createElement("img");
bgImage.src = `images/${chosenImage}`;
bgImage.classList.add("bg-image");

document.body.appendChild(bgImage);