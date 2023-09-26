import { createApp } from 'vue'
import App from './App.vue'
/* import the fontawesome core */
import { library } from '@fortawesome/fontawesome-svg-core'

/* import font awesome icon component */
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

/* import specific icons
* import more icons if needed
* */
import { faXmark } from '@fortawesome/free-solid-svg-icons'
import { faCheck} from "@fortawesome/free-solid-svg-icons";

/* add icons to the library */
library.add(faXmark, faCheck)

createApp(App).component('font-awesome-icon',FontAwesomeIcon).mount('#app')