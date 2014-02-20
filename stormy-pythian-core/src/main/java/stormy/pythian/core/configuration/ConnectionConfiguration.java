/**
 * Copyright 2013-2015 Pierre Merienne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stormy.pythian.core.configuration;

public class ConnectionConfiguration {

    private final ConnectionPoint from;
    private final ConnectionPoint to;

    public ConnectionConfiguration() {
        this.from = new ConnectionPoint();
        this.to = new ConnectionPoint();
    }

    public ConnectionConfiguration(String from, String fromStreamName, String to, String toStreamName) {
        this.from = new ConnectionPoint(from, fromStreamName);
        this.to = new ConnectionPoint(to, toStreamName);
    }

    public boolean isFrom(ComponentConfiguration component) {
        return this.from.hasId(component.getId());
    }

    public boolean isFrom(String componentId) {
        return this.from.hasId(componentId);
    }

    public boolean isTo(String componentId, String streamName) {
        return this.to.equals(new ConnectionPoint(componentId, streamName));
    }

    public ConnectionPoint getFrom() {
        return from;
    }

    public ConnectionPoint getTo() {
        return to;
    }

    public String retrieveFromComponent() {
        return from.id;
    }

    public String retrieveFromStreamName() {
        return from.stream;
    }

    public String retrieveToStreamName() {
        return to.stream;
    }

    public String retrieveToComponent() {
        return to.id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConnectionConfiguration other = (ConnectionConfiguration) obj;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (to == null) {
            if (other.to != null)
                return false;
        } else if (!to.equals(other.to))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ConnectionConfiguration [from=" + from + ", to=" + to + "]";
    }

    public static class ConnectionPoint {

        private String id;
        private String stream;

        public ConnectionPoint() {
        }

        public boolean hasId(String id) {
            return this.id.equals(id);
        }

        public ConnectionPoint(String id, String stream) {
            super();
            this.id = id;
            this.stream = stream;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStream() {
            return stream;
        }

        public void setStream(String stream) {
            this.stream = stream;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((stream == null) ? 0 : stream.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ConnectionPoint other = (ConnectionPoint) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (stream == null) {
                if (other.stream != null)
                    return false;
            } else if (!stream.equals(other.stream))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "ConnectionPoint [id=" + id + ", stream=" + stream + "]";
        }

    }


}
