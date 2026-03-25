var UI = (function() {
    var PHASE_ORDER = ['ANALYSIS', 'CONSENSUS', 'DISSENT', 'SYNTHESIS'];
    var PHASE_NAMES = {
        ANALYSIS: 'Analyse',
        CONSENSUS: 'Konsensbildung',
        DISSENT: 'Dissens (Achter Mann)',
        SYNTHESIS: 'Synthese'
    };

    function show(viewId) {
        var views = document.querySelectorAll('.view');
        for (var i = 0; i < views.length; i++) {
            views[i].classList.remove('active');
        }
        document.getElementById(viewId).classList.add('active');
    }

    function updatePhaseTracker(currentPhase) {
        var steps = document.querySelectorAll('.phase-step');
        var currentIdx = PHASE_ORDER.indexOf(currentPhase);

        for (var i = 0; i < steps.length; i++) {
            var step = steps[i];
            var stepPhase = step.getAttribute('data-phase');
            var stepIdx = PHASE_ORDER.indexOf(stepPhase);

            step.classList.remove('active', 'completed');
            if (stepIdx < currentIdx) {
                step.classList.add('completed');
            } else if (stepIdx === currentIdx) {
                step.classList.add('active');
            }
        }
    }

    function updateProgress(percent, text) {
        var fill = document.getElementById('progress-fill');
        fill.style.width = percent + '%';
        if (text) {
            document.getElementById('progress-text').textContent = text;
        }
    }

    function renderAgents(agents) {
        var panel = document.getElementById('agents-panel');
        panel.innerHTML = '';
        if (!agents) return;

        for (var i = 0; i < agents.length; i++) {
            var agent = agents[i];
            var chip = document.createElement('div');
            chip.className = 'agent-chip' + (agent.eighthMan ? ' eighth-man' : '');
            chip.innerHTML =
                '<span class="agent-name">' + escapeHtml(agent.name) + '</span>' +
                '<span class="agent-role">' + escapeHtml(agent.role) + '</span>';
            panel.appendChild(chip);
        }
    }

    function renderResults(result) {
        // Summary
        document.getElementById('result-summary').textContent = result.summary || 'Keine Zusammenfassung verfügbar.';

        // Consensus score
        var score = result.consensusScore || 0;
        document.getElementById('consensus-bar').style.width = score + '%';
        document.getElementById('consensus-label').textContent = score + '%';

        // Pro arguments
        renderList('result-pro', result.proArguments);

        // Contra arguments
        renderList('result-contra', result.contraArguments);

        // Eighth Man challenge
        document.getElementById('result-eighth-man').textContent =
            result.eighthManChallenge || 'Keine spezifische Kritik formuliert.';

        // Risks
        renderList('result-risks', result.risks);

        // Recommendations
        renderList('result-recommendations', result.recommendations);

        // Phase details
        renderPhaseDetails(result.phaseResults);
    }

    function renderList(elementId, items) {
        var el = document.getElementById(elementId);
        el.innerHTML = '';
        if (!items || items.length === 0) {
            el.innerHTML = '<li>Keine Einträge</li>';
            return;
        }
        for (var i = 0; i < items.length; i++) {
            var li = document.createElement('li');
            li.textContent = items[i];
            el.appendChild(li);
        }
    }

    function renderPhaseDetails(phaseResults) {
        var container = document.getElementById('phase-details');
        container.innerHTML = '';
        if (!phaseResults) return;

        for (var i = 0; i < phaseResults.length; i++) {
            var pr = phaseResults[i];
            var phaseName = PHASE_NAMES[pr.phase] || pr.phase;

            var item = document.createElement('div');
            item.className = 'accordion-item';

            var header = document.createElement('div');
            header.className = 'accordion-header';
            header.innerHTML = '<span>Phase ' + (i + 1) + ': ' + escapeHtml(phaseName) + '</span>' +
                '<span class="arrow">&#9660;</span>';
            header.addEventListener('click', toggleAccordion);

            var body = document.createElement('div');
            body.className = 'accordion-body';

            if (pr.responses) {
                for (var j = 0; j < pr.responses.length; j++) {
                    var resp = pr.responses[j];
                    var respDiv = document.createElement('div');
                    respDiv.className = 'agent-response';

                    var respHeader = document.createElement('div');
                    respHeader.className = 'agent-response-header' + (resp.eighthMan ? ' eighth-man' : '');
                    respHeader.textContent = resp.agentName + ' (' + resp.agentRole + ')';

                    var respContent = document.createElement('div');
                    respContent.className = 'agent-response-content';
                    respContent.textContent = resp.content;

                    respDiv.appendChild(respHeader);
                    respDiv.appendChild(respContent);
                    body.appendChild(respDiv);
                }
            }

            item.appendChild(header);
            item.appendChild(body);
            container.appendChild(item);
        }
    }

    function toggleAccordion() {
        var item = this.parentElement;
        item.classList.toggle('open');
    }

    function showError(message) {
        document.getElementById('error-message').textContent = message;
        show('error-view');
    }

    function escapeHtml(str) {
        if (!str) return '';
        var div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    return {
        show: show,
        updatePhaseTracker: updatePhaseTracker,
        updateProgress: updateProgress,
        renderAgents: renderAgents,
        renderResults: renderResults,
        showError: showError
    };
})();
