<h1>Discord Bot Application</h1>
<h2>Overview</h2>
<p>The <strong>Discord Bot Application</strong> is a versatile bot designed to enhance your Discord server experience. It offers a range of features including playing the Mafia game, checking the weather, interacting with ChatGPT, playing and managing music, and adding/viewing jokes. This bot is built using Java with Spring Boot and integrates with the Discord API via JDA (Java Discord API).</p>

<h2>Features</h2>
    <ul>
        <li><strong>Mafia Game:</strong> Create and join lobbies to play the Mafia game with your friends.</li>
        <li><strong>Weather Updates:</strong> Check the current weather for any city using the <code>/weather</code> command.</li>
        <li><strong>ChatGPT Integration:</strong> Interact with ChatGPT directly within Discord using the <code>/chatgpt</code> command.</li>
        <li><strong>Music Player:</strong> Play, queue, skip, and manage music in your voice channels.</li>
        <li><strong>Jokes:</strong> Add your own jokes and view a random joke from the bot’s collection.</li>
    </ul>

 <h3>Prerequisites</h3>
    <ul>
        <li>Java 17 or higher</li>
        <li>Maven</li>
<li>A Discord bot token (Create a bot via the <a href="https://discord.com/developers/applications" target="_blank">Discord Developer Portal</a>)</li>
<li>An OpenWeatherMap API key (For the weather feature)</li>
        <li>A <code>.env</code> file in the root directory containing the following:</li>
    </ul>
    <div class="code-block">
        <pre><code>TOKEN=your-discord-bot-token
        WEATHER_TOKEN=your-openweathermap-api-key
OPENAI_API_KEY=your-openapi-api-key</code></pre>
    </div>
    <h3>Commands</h3>
    <ul>
        <li><strong>Mafia Game:</strong>
            <ul>
                <li><code>/createlobby [players]</code>: Create a new lobby for the Mafia game.</li>
                <li><code>/joinlobby</code>: Join an existing lobby.</li>
            </ul>
        </li>
        <li><strong>Weather:</strong>
            <ul>
                <li><code>/weather [city]</code>: Get the current weather for the specified city.</li>
            </ul>
        </li>
        <li><strong>ChatGPT:</strong>
            <ul>
                <li><code>/chatgpt</code>: Open a modal to send a query to ChatGPT.</li>
            </ul>
        </li>
        <li><strong>Music:</strong>
            <ul>
                <li><code>/play [song name or URL]</code>: Play a song in the voice channel.</li>
                <li><code>/nowplaying</code>: Display the current song playing.</li>
                <li><code>/queue</code>: View the current music queue.</li>
                <li><code>/repeat</code>: Toggle repeating the current song.</li>
                <li><code>/skip</code>: Skip the current song.</li>
                <li><code>/stop</code>: Stop the music and clear the queue.</li>
            </ul>
        </li>
        <li><strong>Jokes:</strong>
            <ul>
                <li><code>/addjoke [joke]</code>: Add a new joke.</li>
                <li><code>/joke</code>: Display a random joke.</li>
            </ul>
        </li>
    </ul>
