<script>
    import { onMount } from "svelte";
    import HiddenInput from "../hidden-input.svelte";

    let developerApiKey = "";
    let conductorKey = "";
    let conductorPort = "";

    onMount(async () => {
        const prefs = (await Bridge.query("app:preferences")).data;

        developerApiKey = prefs.developerApiKey;
        conductorKey = prefs.conductorKey;
        conductorPort = prefs.conductorPort;
    });
</script>

<!-- svelte-ignore a11y-label-has-associated-control -->
<div class="no-select developer-stuff">
    <HiddenInput label="Api Key" bind:value={developerApiKey} />
    <HiddenInput label="Conductor Key" bind:value={conductorKey} />
    <div class="field">
        <label class="label">Conductor port</label>
        <div class="control">
            <input class="input" type="input" readonly value={conductorPort} />
        </div>
    </div>
</div>

<style>
    .developer-stuff {
        width: 400px;
    }
</style>
