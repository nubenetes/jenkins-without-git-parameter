// ==============================================================================
// Shared Library Step: otelLogEvent.groovy
// Emits OpenTelemetry trace metadata & annotations for Grafana correlation
// ==============================================================================

def call(Map eventData = [:]) {
    def eventName = eventData.name ?: 'pipeline.custom.event'
    
    echo "📊 [OpenTelemetry Event] Emitting Span Annotation: ${eventName}"
    eventData.each { k, v ->
        echo "   - otel.attr.${k} = ${v}"
    }

    // Capture or inject W3C Trace Context (traceparent format: 00-traceid-spanid-01)
    def traceId = env.TRACE_ID ?: (env.OTEL_TRACE_ID ?: "4bf92f3577b34da6a3ce929d0e0e4736")
    def spanId  = env.SPAN_ID  ?: (env.OTEL_SPAN_ID  ?: "00f067aa0ba902b7")
    def traceparent = "00-${traceId}-${spanId}-01"

    echo "🔗 [W3C Trace Context] Propagating: TRACEPARENT=${traceparent}"
    
    // Export to pipeline environment for downstream curl / API / ArgoCD calls
    env.CURRENT_TRACEPARENT = traceparent
    env.CURRENT_TRACE_ID    = traceId

    // When the Jenkins OpenTelemetry plugin is active, span attributes are automatically attached
    // to the active root and child spans and forwarded to the OTel Collector.
}

